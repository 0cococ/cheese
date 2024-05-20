package coco.cheese.core.utils

import android.provider.Settings
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.hjq.window.EasyWindow
import java.lang.ref.WeakReference

class FloatyUtils(private val env: Env) {


    fun floatWith(): EasyWindow<*> {
        return EasyWindow.with(env.application)
    }
    fun requestPermission(timeout:Int): Boolean {
        return PermissionsUtils.get().requestPermission(FLOAT,timeout)
    }

    fun checkPermission(): Boolean {
        return PermissionsUtils.get().checkPermission(FLOAT)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<FloatyUtils>? = null
        private var instance: FloatyUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): FloatyUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = FloatyUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): FloatyUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(FloatyUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}