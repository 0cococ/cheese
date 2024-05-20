package coco.cheese.core.utils

import android.icu.text.SimpleDateFormat
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.lang.ref.WeakReference
import java.util.Date
import java.util.Locale

class TimeUtils(env: Env) {

    fun timeFormat (timestamp: Long, pn: String): String {
        val sdf = SimpleDateFormat(pn, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    fun getTime(): Long {
        return System.currentTimeMillis()
    }
    companion object : IBase {
        private var instanceWeak: WeakReference<TimeUtils>? = null
        private var instance: TimeUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): TimeUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = TimeUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): TimeUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(TimeUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}