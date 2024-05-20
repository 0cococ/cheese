package coco.cheese.core.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.activity.result.ActivityResultLauncher
import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.andro_accessibility_api.requireBaseAccessibility
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.elvishew.xlog.XLog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.Toaster
import java.io.File
import java.lang.ref.WeakReference
const val ACCESSIBILITY = 1
const val FLOAT = 2
const val RECORDSCREEN = 3
const val ROOT = 4
class PermissionsUtils(private val env: Env) {



    fun requestPermission(permission: Int, timeout: Int): Boolean {
        return when (permission) {
            ACCESSIBILITY -> requestAccessibilityPermission(timeout)
            FLOAT -> requestFloatPermission(timeout)
            RECORDSCREEN -> requestRecordScreenPermission(timeout)
            ROOT ->requestRootPermission(timeout)
            else -> false
        }
    }

    fun checkPermission(permission: Int): Boolean {
        return when (permission) {
            ACCESSIBILITY -> checkAccessibilityPermission()
            FLOAT -> checkFloatPermission()
            RECORDSCREEN -> checkRecordScreenPermission()
            ROOT -> checkRootPermission()
            else -> {
                XLog.e("未知权限码")
                false
            }
        }
    }

    fun requestAllPermissions(block: () -> Unit) {
        XXPermissions.with(env.context)
            .permission(Permission.GET_INSTALLED_APPS)
            .permission(Permission.SCHEDULE_EXACT_ALARM)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.READ_PHONE_STATE)
            .permission(Permission.SYSTEM_ALERT_WINDOW)
            .permission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        Toaster.show("获取部分权限成功，但部分权限未正常授予")
                        return
                    }
                    Toaster.show("所需权限正常")
                    block()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        Toaster.show("被永久拒绝授权，请手动授予权限")
                        XXPermissions.startPermissionActivity(env.context, permissions)
                    } else {
                        Toaster.show("获取所需权限失败")
                    }
                }
            })
    }

    fun screenRecord(resultCode: Int, data: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK) {
            Log.d("~~~", "User cancelled")
            return false
        }
        Log.d("~~~", "Starting screen capture")
        val mediaProjectionManager = env.recordScreen.mediaProjectionManager
        val mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data!!)
        env.recordScreen.mediaProjection = mediaProjection
        return true
    }

    private fun requestFloatPermission(timeout: Int): Boolean {
        if (checkFloatPermission()) {
            return true
        }
        if (!Settings.canDrawOverlays(env.activity)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + env.activity.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            env.activity.startActivity(intent)
        }
        for (i in 0 until timeout * 10) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            if (checkAccessibilityPermission()) {
                return true
            }
        }
        return false
    }

    private fun checkFloatPermission(): Boolean {
        return Settings.canDrawOverlays(env.context)
    }

    fun requestRootPermission(timeout: Int): Boolean {
        return try {
            val cmd = "chmod 777 ${Env.get().context.packageCodePath}"
             Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).apply {
                outputStream.bufferedWriter().use { it.write("exit\n") }
                waitFor()
            }.exitValue()
//            ==0
            for (i in 0 until timeout * 10) {
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
                if (checkRootPermission()) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            false
        }
    }

    fun checkRootPermission(): Boolean {
        return try {
            val file1 = File("/system/bin/su")
            val file2 = File("/system/xbin/su")
            (file1.exists() && file1.canExecute()) || (file2.exists() && file2.canExecute())
        } catch (e: Exception) {
            false
        }
    }


    private fun requestAccessibilityPermission(timeout: Int): Boolean {
        if (checkAccessibilityPermission()) {
            return true
        }
        cn.vove7.auto.core.utils.jumpAccessibilityServiceSettings(AccessibilityApi.BASE_SERVICE_CLS)
        for (i in 0 until timeout * 10) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            if (checkAccessibilityPermission()) {
                return true
            }
        }
        return false

    }

    private fun checkAccessibilityPermission(): Boolean {
        val accessibilityManager =
            env.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val packageName = env.context.packageName
        var isServiceEnabled = false
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        for (service in enabledServices) {
            if (service.resolveInfo.serviceInfo.packageName == packageName) {
                isServiceEnabled = true
                break
            }
        }

        return isServiceEnabled
    }

    private fun requestRecordScreenPermission(timeout: Int): Boolean {
        RecordScreenUtils.get()
        if (checkRecordScreenPermission()) {
            return true
        }
        Log.d("~~~", "Requesting confirmation")
        val intent = env.recordScreen.mediaProjectionManager?.createScreenCaptureIntent()
        (env.activityResultLauncher["1"] as ActivityResultLauncher<Intent>).launch(intent)
        for (i in 0 until timeout * 10) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            if (checkRecordScreenPermission()) {
                return true
            }
        }
        return false
    }

    private fun checkRecordScreenPermission(): Boolean {
        return env.recordScreen.mediaProjection != null
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<PermissionsUtils>? = null
        private var instance: PermissionsUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): PermissionsUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PermissionsUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PermissionsUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PermissionsUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }


    }


}