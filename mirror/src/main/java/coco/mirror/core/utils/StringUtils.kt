package coco.mirror.core.utils

import android.content.Context
import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import java.io.File
import java.lang.ref.WeakReference

class StringUtils(private val env: Env) {
    fun isFilePath(path: String): Boolean {
        val fileName = path.substringAfterLast("/")
        return fileName.contains(".")
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