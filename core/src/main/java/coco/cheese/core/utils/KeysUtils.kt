package coco.cheese.core.utils


import android.os.Build
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.lang.ref.WeakReference

class KeysUtils(private val env: Env) {

    fun home(): Boolean {
        return cn.vove7.auto.core.api.home()
    }
    fun back(): Boolean {
        return cn.vove7.auto.core.api.back()
    }
    fun quickSettings(): Boolean {
        return  cn.vove7.auto.core.api.quickSettings()
    }
    fun powerDialog(): Boolean {
        return cn.vove7.auto.core.api.powerDialog()
    }
    fun pullNotificationBar(): Boolean {
        return cn.vove7.auto.core.api.pullNotificationBar()
    }
    fun recents(): Boolean {
        return cn.vove7.auto.core.api.recents()
    }
    fun lockScreen(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cn.vove7.auto.core.api.lockScreen()
        } else false
    }
    fun screenShot(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cn.vove7.auto.core.api.screenShot()
        } else false
    }
    fun splitScreen(): Boolean {
        return  cn.vove7.auto.core.api.splitScreen()
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<KeysUtils>? = null
        private var instance: KeysUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): KeysUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = KeysUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): KeysUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(KeysUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}