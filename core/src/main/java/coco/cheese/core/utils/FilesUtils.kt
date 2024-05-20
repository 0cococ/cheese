package coco.cheese.core.utils

import android.graphics.Bitmap
import android.os.Environment
import cn.vove7.auto.core.viewfinder.ConditionGroup
import cn.vove7.auto.core.viewnode.ViewNode
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import okio.IOException
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference

class FilesUtils(private val env: Env) {

    fun findFile(directory: File, fileName: String): File? {
        val file = File(directory, fileName)
        if (file.exists()) {
            return file
        }

        val subDirectories = directory.listFiles { file -> file.isDirectory } ?: return null
        for (subDirectory in subDirectories) {
            val foundFile = findFile(subDirectory, fileName)
            if (foundFile != null) {
                return foundFile
            }
        }
        return null
    }

    fun read(path: String): Array<String>? {
        if (isFile(path)){
            return  readFile(path)
        }
        return readFolder(path)
    }
    fun rm(path:String): Boolean {
        if (isFile(path)){
            return  rmFile(path)
        }
        return rmFolder(path)
    }
    fun create(path: String): Boolean {
        if (StringUtils.get().isFilePath(path)){
            return  createFile(path)
        }
        return  createFolder(path)
    }
    fun copy(sourceDirPath: String, destinationDirPath: String): Boolean {
        if (isFile(sourceDirPath)){
            return copyFile(sourceDirPath,destinationDirPath)
        }
        return  copyFolder(sourceDirPath,destinationDirPath)
    }

    fun save(obj: Any, filePath: String): Boolean {
       return when (obj) {
            is InputStream -> streamSaveSD(obj,filePath)
            is Bitmap ->  bitmapSaveSD(obj,filePath)
            else -> false
        }
    }
    fun readJson(filePath: String, key: String): String? {
        val gson = Gson()
        return try {
            File(filePath).bufferedReader().use { reader ->
                val jsonContent = reader.readText()
                val jsonObject = gson.fromJson(jsonContent, Map::class.java)
                jsonObject[key]?.toString()
            }
        } catch (e: FileNotFoundException) {
            println("文件未找到: $filePath")
            null
        } catch (e: IOException) {
            println("读取文件时出错: $e")
            null
        } catch (e: Exception) {
            println("发生异常: $e")
            null
        }
    }
    fun isFile(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }
    fun isFolder(folderPath: String): Boolean {
        val directory = File(folderPath)
        return directory.exists() && directory.isDirectory
    }
    fun append(filePath: String, content: String): Boolean {
        var writer: BufferedWriter? = null
        return try {
            // 创建 BufferedWriter 对象
            writer = BufferedWriter(FileWriter(filePath, true))
            // 写入内容并换行
            writer.write(content + System.lineSeparator())
            // 刷新缓存区
            writer.flush()
            true
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            false
        } finally {
            // 关闭流
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun write(filePath: String, content: String): Boolean {
        var writer: FileWriter? = null
        return try {
            // 创建 FileWriter 对象
            writer = FileWriter(filePath, false)
            // 写入内容
            writer.write(content)
            // 刷新缓存区
            writer.flush()
            true
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            false
        } finally {
            // 关闭流
            if (writer != null) {
                try {
                    writer.close()
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun copyFolder(sourceDirPath: String, destinationDirPath: String): Boolean {
        val sourceDir = File(sourceDirPath)
        val destinationDir = File(destinationDirPath)

        if (!sourceDir.exists() || !sourceDir.isDirectory) {
            println("Source directory does not exist or is not a directory.")
            return false
        }

        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }

        val files = sourceDir.listFiles() ?: return true

        for (file in files) {
            val sourceFilePath = file.absolutePath
            val destinationFilePath = "${destinationDir.absolutePath}/${file.name}"
            if (file.isDirectory) {
                copyFolder(sourceFilePath, destinationFilePath)
            } else {
                copyFile(sourceFilePath, destinationFilePath)
            }
        }
        return true
    }
    private fun copyFile(sourceFilePath: String, destinationFilePath: String): Boolean {
        val sourceFile = File(sourceFilePath)
        val destinationFile = File(destinationFilePath)

        if (!sourceFile.exists() || !sourceFile.isFile) {
            println("Source file does not exist or is not a file.")
            return false
        }

//        if (destinationFile.exists()) {
//            println("Destination file already exists.")
//            return false
//        }

        try {
            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
            println("File copied successfully.")
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
    private fun createFolder(folderPath: String): Boolean {
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory()) {
            // 文件夹已存在，不执行任何操作
            return true
        }
        return  folder.mkdirs()
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
    private fun readFolder(folderPath:String): Array<String>?{
        val fileNames = mutableListOf<String>()
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) {
            return null
        }
        listFilesRecursively(folder, fileNames)
        return fileNames.toTypedArray()
    }
    private fun readFile(filePath: String) :Array<String>?{
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }

        return try {
            val linesList = mutableListOf<String>()
            FileReader(file).buffered().use { reader ->
                reader.forEachLine { line ->
                    linesList.add(line)
                }
            }
            linesList.toTypedArray()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    private fun rmFolder(folderPath: String): Boolean {
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) {
            return false
        }
        folder.listFiles()?.forEach { file ->
            if (file.isFile) {
                if (!file.delete()) {
                    return false
                }
            }
            else if (file.isDirectory) {
                if (!rmFolder(file.absolutePath)) {
                    return false
                }
            }
        }
        return folder.delete()
    }
    private fun rmFile(filePath: String): Boolean {
        val file = File(filePath)
        return try {
            file.delete()
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }
    private fun listFilesRecursively(folder: File, fileNames: MutableList<String>) {
        val files = folder.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isFile()) {
                    fileNames.add(file.getName())
                } else if (file.isDirectory()) {
                    listFilesRecursively(file, fileNames)
                }
            }
        }
    }

    fun streamSaveSD(inputStream: InputStream, filePath: String): Boolean {
        try {
            val outputStream: OutputStream = FileOutputStream(filePath)
            val buffer = ByteArray(4096)
            var length: Int
            while (inputStream.read(buffer).also { length = it } != -1) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            return true
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: java.io.IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun bitmapSaveSD(bitmap: Bitmap, filePath: String): Boolean {
        var result = false
        // 首先检查SD卡是否可用
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            var fos: FileOutputStream? = null
            try {
                // 创建目录
                val file = File(filePath)
                val parentFile = file.getParentFile()
                if (!parentFile!!.exists()) {
                    parentFile.mkdirs()
                }

                // 将Bitmap保存到文件
                fos = FileOutputStream(file)
                result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (fos != null) {
                        fos.flush()
                        fos.close()
                    }
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<FilesUtils>? = null
        private var instance: FilesUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : FilesUtils {
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