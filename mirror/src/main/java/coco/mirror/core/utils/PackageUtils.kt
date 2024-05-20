package coco.mirror.core.utils

import android.content.pm.PackageManager
import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import java.lang.ref.WeakReference

class PackageUtils(private val env: Env) {


    fun extractPackageName(apkFilePath: String): String? {
        val pm: PackageManager = Env.get().context.packageManager
        val packageInfo = pm.getPackageArchiveInfo(apkFilePath, 0)
        return packageInfo?.packageName
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<PackageUtils>? = null
        private var instance: PackageUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : PackageUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PackageUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PackageUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PackageUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}