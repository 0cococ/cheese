package coco.cheese.core.utils

import android.content.Context
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.io.File
import java.lang.ref.WeakReference

class StringUtils(private val env: Env) {
    lateinit var vector:String;
    lateinit var cipherText:String;
    external fun add(str:String): String
    external fun dispel(ciphertext:String,vector:String): String
    fun addStringFog(str:String): StringUtils {
        val s=add(str)
        val parts = s.split(",")
        cipherText=parts[0]
        vector=parts[1]
        return this
    }
    fun dispelStringFog(ciphertext:String,vector:String): String {
        return dispel(ciphertext,vector)
    }
    fun isFilePath(path: String): Boolean {
        val fileName = path.substringAfterLast("/")
        return fileName.contains(".")
    }
    fun isPathInStorage(str: String): Boolean {
        return str.startsWith("/storage/")
    }
    fun parsePaths(currentPath: String, relativePath: String): String {
        return File(currentPath).parentFile!!.resolve(relativePath).canonicalPath
    }

    fun fileNameToJs(fileName: String): String {
        return when {
            fileName.endsWith(".ts") -> fileName.replace(".ts", ".js")
            fileName.endsWith(".js") -> fileName // 如果文件名已经是以 ".js" 结尾，则直接返回原文件名
            else -> "$fileName.js" // 如果文件名没有后缀，则在末尾添加 ".js" 后缀
        }
    }


    fun getRandomString(length: Int): String =
        (1..length)
            .map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }
            .joinToString("")

    companion object : IBase {
        private var instanceWeak: WeakReference<StringUtils>? = null
        private var instance: StringUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : StringUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = StringUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): StringUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(StringUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}