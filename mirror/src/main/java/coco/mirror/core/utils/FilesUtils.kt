package coco.mirror.core.utils

import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.security.MessageDigest

class FilesUtils(private val env: Env) {
    fun calculateSha1(file:File): String {
        val digest = MessageDigest.getInstance("SHA-1")
        file.inputStream().use { stream ->
            val buffer = ByteArray(8192)
            generateSequence { stream.read(buffer).takeIf { it != -1 } }
                .forEach { bytesRead -> digest.update(buffer, 0, bytesRead) }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun isFile(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }
    fun isFolder(folderPath: String): Boolean {
        val directory = File(folderPath)
        return directory.exists() && directory.isDirectory
    }
    fun create(path: String): Boolean {
        if (StringUtils.get().isFilePath(path)) {
            return createFile(path)
        }
        return createFolder(path)
    }

    private fun createFolder(folderPath: String): Boolean {
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory()) {
            // 文件夹已存在，不执行任何操作
            return true
        }
        return folder.mkdirs()
    }

    private fun createFile(filePath: String): Boolean {
        val file = File(filePath)
        return if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    fun copyFiles(originFilePath: String?, endFilePath: String) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = FileInputStream(originFilePath)
            out = FileOutputStream(endFilePath)
            val bytes = ByteArray(1024)
            var i: Int
            while (`in`.read(bytes).also { i = it } != -1) out.write(bytes, 0, i)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<FilesUtils>? = null
        private var instance: FilesUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): FilesUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = FilesUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): FilesUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(FilesUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }
    }

}