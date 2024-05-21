package coco.cheese.core.utils

import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import coco.mirror.core.utils.APP_DIRECTORY
import coco.mirror.core.utils.APP_THAT_THIS
import java.io.File
import java.lang.ref.WeakReference

class PluginsUtils(private val env: Env) {
    fun install(path:String): Boolean {
        coco.mirror.core.manger.PackageManager.get().install(path)
        if (APP_THAT_THIS.exists() && APP_THAT_THIS.isFile) {
            return true
        }else{
            return false
        }
    }

    fun start() {
        coco.mirror.core.manger.PackageManager.get().load()
    }
    fun uninstall(pkg:String): Boolean {
        val file = File(APP_DIRECTORY,pkg)
        file.deleteRecursively()
        if (!file.exists()){
            return true
        }
        return false
    }
    companion object : IBase {
        private var instanceWeak: WeakReference<PluginsUtils>? = null
        private var instance: PluginsUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): PluginsUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PluginsUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PluginsUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PluginsUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}