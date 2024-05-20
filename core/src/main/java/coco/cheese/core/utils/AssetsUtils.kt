package coco.cheese.core.utils

import android.content.Context
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.Stack

class AssetsUtils(private val env: Env) {
    fun read(path: String): String {
        val newPath = if (path.startsWith("/")) {
            path.substring(1)
        } else {
            path
        }
        if (!env.runTime.runMode) {
            val filePath = "${ASSETS_DIRECTORY()}/$newPath"
            return FilesUtils.get().read(filePath)?.joinToString(" ") ?: ""
        }
        if (isFile(newPath)) {
            return readFile(newPath)
        }
        return readFolder(newPath).joinToString(" ")

    }

    fun copy(path: String, destPath: String): Boolean {
        val newPath = if (path.startsWith("/")) {
            path.substring(1)
        } else {
            path
        }
        if (!env.runTime.runMode) {
            val filePath = "${ASSETS_DIRECTORY()}/$newPath"
            return FilesUtils.get().copy(filePath, destPath)
        }
        if (isFile(newPath)) {
            return copyFileToSD(newPath, destPath)
        }
        return copyFolderToSD(newPath, destPath)
    }

    private fun readFolder(path: String): Array<String> {
        val folderName_ = path

        val fileList = mutableListOf<String>()
        try {
            val stack = Stack<String>()
            stack.push(folderName_)

            while (stack.isNotEmpty()) {
                val currentPath = stack.pop()

                val list = env.context.assets.list(currentPath) ?: continue

                for (item in list) {
                    val subPath = if (currentPath.isEmpty()) item else "$currentPath/$item"

                    try {
                        env.context.assets.open(subPath).use { _ ->
                            // 如果成功打开文件流，说明是文件而非目录
                            fileList.add(subPath)
                        }
                    } catch (e: IOException) {
                        // 报错说明是目录，将子目录路径入栈，以便后续处理
                        stack.push(subPath)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fileList.toTypedArray()
    }

    private fun readFile(fileName: String): String {
        val fileName_ = fileName

        return env.context.assets.open(fileName_).use { inputStream ->
            inputStream.readBytes().toString(Charsets.UTF_8)
        }

    }

    private fun copyFileToSD(fileName: String, destPath: String): Boolean {
        val fileName_ = fileName
        val assetManager = env.context.assets
        val destFile = File(destPath, fileName_)
        return try {
            val inputStream = assetManager.open(fileName_)
            val outputStream = FileOutputStream(destFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                }
            }

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun copyFolderToSD(sourceFolder: String, destFolder: String): Boolean {

        val sourceFolder_ = sourceFolder


        return try {
            val assetManager = env.context.assets
            val files = assetManager.list(sourceFolder_) ?: return false

            val targetFolder = File(destFolder).apply {
                if (exists()) deleteRecursively()
                mkdirs()
            }
            for (filename in files) {
                val fullPath = "$sourceFolder_/$filename"
                if (isFolder(fullPath)) {
                    copyFolderToSD(fullPath, "$destFolder/$filename")
                } else {
                    assetManager.open(fullPath).use { inputStream ->
                        File(targetFolder, filename).outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun isFolder(folderPath: String): Boolean {
        val newPath = if (folderPath.startsWith("/")) {
            folderPath.substring(1)
        } else {
            folderPath
        }
        if (!env.runTime.runMode) {
            return FilesUtils.get().isFolder("${ASSETS_DIRECTORY()}/$newPath")
        }

        val assetManager = env.context.assets
        try {
            val files = assetManager.list(folderPath)
            return !files.isNullOrEmpty()
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        }
        return false
    }

    fun isFile(filePath: String): Boolean {
        val newPath = if (filePath.startsWith("/")) {
            filePath.substring(1)
        } else {
            filePath
        }
        if (!env.runTime.runMode) {
            return FilesUtils.get().isFile("${ASSETS_DIRECTORY()}/$newPath")
        }

        return try {
            val inputStream = env.context.assets.open(filePath)
            inputStream.close()
            true
        } catch (e: IOException) {
            false
        }
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<AssetsUtils>? = null
        private var instance: AssetsUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): AssetsUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = AssetsUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): AssetsUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(AssetsUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}