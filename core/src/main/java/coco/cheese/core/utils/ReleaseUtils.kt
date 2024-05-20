package coco.cheese.core.utils

import android.graphics.Bitmap
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference

class ReleaseUtils(val env: Env) {
    fun <T> release(resource: T): Boolean {
        try {
            println(resource)
            when (resource) {
                is InputStream -> releaseInputStream(resource)
                is Bitmap -> releaseBitmap(resource)
                else -> return false
            }
            return true
        } catch (e: Exception) {
            println("Failed to release resource: ${e.message}")
        }
        return false
    }


    fun releaseInputStream(inputStream: InputStream?) {
        inputStream?.close()
    }

    fun releaseBitmap(bitmap: Bitmap?) {
        bitmap?.apply {
            if (!isRecycled) {
                recycle()
            }
        }
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ReleaseUtils>? = null
        private var instance: ReleaseUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ReleaseUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ReleaseUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ReleaseUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ReleaseUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}