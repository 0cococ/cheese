package coco.cheese.core

import android.R
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.ContextThemeWrapper
import cn.vove7.andro_accessibility_api.AccessibilityApi
import coco.cheese.core.service.AccessibilityService
import coco.cheese.core.service.ForegroundService
import coco.cheese.core.utils.LOG_DIRECTORY
import coco.cheese.core.utils.ProcessUtils
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.DefaultFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.LevelFileNameGenerator
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.hjq.toast.Toaster
import com.tencent.bugly.crashreport.CrashReport
import me.weishu.reflection.Reflection
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.opencv.android.OpenCVLoader

open class App : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            AndroidLogger(Level.DEBUG)
            modules(appModule)
            androidContext(this@App)
        }
//        Env.get().context = ContextThemeWrapper(this, R.style.Theme)
        Env.get().context=this
        coco.mirror.core.Env.get().context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            startService(Intent(this, ForegroundService::class.java))
        }
        Toaster.init(this);
        DeviceIdentifier.register(this)
        AccessibilityApi.init(this, AccessibilityService::class.java)
        val filePrinter: Printer = FilePrinter.Builder(LOG_DIRECTORY.path)
            .fileNameGenerator(LevelFileNameGenerator())
            .backupStrategy(NeverBackupStrategy())
            .cleanStrategy(FileLastModifiedCleanStrategy(30L * 24L * 60L * 60L * 1000L))
            .flattener(DefaultFlattener())
            .build()
        val androidPrinter = AndroidPrinter(true);
        val config = LogConfiguration.Builder()
            .tag("")
            .enableBorder()
            .build()
        XLog.init(config, androidPrinter, filePrinter)
        CrashReport.initCrashReport(this, "9708bb6d42", false)
        OpenCVLoader.initLocal();
        CrashHandler.getInstance()

    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Reflection.unseal(base);
    }

    private val appModule = module {
        single { Env() }
        single { coco.mirror.core.Env() }
    }
}