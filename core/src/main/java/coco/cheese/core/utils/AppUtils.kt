package coco.cheese.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.lang.ref.WeakReference

class AppUtils(private val env: Env) {
    fun openUrl(url: String) {
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else {
            url
        }
        env.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun uninstall(packageName: String) {
        env.context.startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun getPackageName(appName: String): String? {
        val packageManager =  env.context.applicationContext.packageManager
        val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString() == appName) {
                return applicationInfo.packageName
            }
        }
        return null
    }
    fun getAppName(packageName: String): String? {
        val packageManager = env.context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo)
            appName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    fun openAppSettings(packageName: String): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.setData(Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            env.context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        }
    }
//    throw PackageManager.NameNotFoundException("应用程序未安装")
    fun openApp(packageName: String): Boolean {
        val packageManager = env.context.applicationContext.packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            env.context. startActivity(intent)
            true
        } else {
            false
        }
    }

    fun openScheme(context: Context=env.context, schemeUri: String): Boolean {
        if (schemeUri.isEmpty()) {
            return false
        }
        val uri = Uri.parse(schemeUri) ?: return false
        val intent = Intent(Intent.ACTION_VIEW, uri)
        return if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            true
        } else {
            false
        }
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<AppUtils>? = null
        private var instance: AppUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : AppUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = AppUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): AppUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(AppUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}