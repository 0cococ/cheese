package coco.cheese.core

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.text.TextUtils
import coco.cheese.core.utils.ProcessUtils
import com.elvishew.xlog.XLog
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Field
import java.util.Timer
import java.util.TimerTask
import kotlin.system.exitProcess

object CrashHandler : Thread.UncaughtExceptionHandler {
    private var mInstance: CrashHandler? = null
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private const val TAG = "CrashHandler"

    init {
        XLog.tag(TAG).d("开启全局异常捕获器")
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        XLog.tag(TAG).d("捕获异常任务开始")
        try {
            val info = collectDeviceInfo(Env.get().applicationContext, ex)
            XLog.tag(TAG).d("捕获到未定义异常")
            saveInfo(info)

            ProcessUtils.get().exit()

        } catch (e: Exception) {
            // 异常处理过程中发生了异常，打印错误信息
            XLog.tag(TAG).e("捕获未捕获异常时发生了异常", e)
        } finally {
            XLog.tag(TAG).d("捕获异常任务完成")
//             让系统默认的异常处理器处理异常
//            mDefaultHandler?.uncaughtException(thread, ex)
        }
    }

    private fun collectDeviceInfo(c: Context, ex: Throwable): String {
        val infos = HashMap<String, String>()
        try {
            val pm: PackageManager = c.packageManager
            val pi = pm.getPackageInfo(c.packageName, PackageManager.GET_ACTIVITIES)
            pi?.let {
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pi.getLongVersionCode().toString()
                } else {
                    pi.versionCode.toString()
                }
                val versionName =
                    if (TextUtils.isEmpty(pi.versionName)) "没有版本名称" else pi.versionName
                infos["versionCode"] = versionCode
                infos["versionName"] = versionName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        // 收集 Build 类的字段信息
        Build::class.java.declaredFields.forEach { field ->
            try {
                field.isAccessible = true
                infos[field.name] = field.get(null)?.toString() ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 收集异常信息
        val result = StringWriter().use { writer ->
            PrintWriter(writer).use { printWriter ->
                ex.printStackTrace(printWriter)
                var cause: Throwable? = ex.cause
                while (cause != null) {
                    cause.printStackTrace(printWriter)
                    cause = cause.cause
                }
            }
            writer.toString()
        }

        // 构建最终字符串
        return buildString {
            infos.forEach { (key, value) ->
                append("$key=$value\n")
            }
            append(result)
        }
    }


//    private fun collectDeviceInfo(c: Context, ex: Throwable): String {
//        val infos = HashMap<String, String>()
//        try {
//            val pm: PackageManager = c.packageManager
//            val pi = pm.getPackageInfo(c.packageName, PackageManager.GET_ACTIVITIES)
//            pi?.let {
//                val versionCode = pi.versionCode.toString()
//                val versionName = if (TextUtils.isEmpty(pi.versionName)) "没有版本名称" else pi.versionName
//                infos["versionCode"] = versionCode
//                infos["versionName"] = versionName
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//        val fields: Array<Field> = Build::class.java.declaredFields
//        for (field in fields) {
//            try {
//                field.isAccessible = true
//                infos[field.name] = field.get(null)!!.toString()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//        val writer: Writer = StringWriter()
//        val printWriter = PrintWriter(writer)
//        ex.printStackTrace(printWriter)
//        var cause: Throwable? = ex.cause
//        while (cause != null) {
//            cause.printStackTrace(printWriter)
//            cause = cause.cause
//        }
//        printWriter.close()
//        val result: String = writer.toString()
//        val sb = StringBuffer()
//        for ((key, value) in infos) {
//            sb.append("$key=$value\n")
//        }
//        sb.append(result)
//        return sb.toString()
//    }

    private fun saveInfo(infos: String) {
        XLog.tag(TAG).e(infos)
    }

    fun getInstance(): CrashHandler {
        if (mInstance == null) {
            synchronized(CrashHandler::class.java) {
                if (mInstance == null) {
                    mInstance = CrashHandler
                }
            }
        }
        return mInstance!!
    }
}