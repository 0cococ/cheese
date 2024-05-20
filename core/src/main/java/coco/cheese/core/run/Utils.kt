package coco.cheese.core.run

import coco.cheese.core.Env
import coco.cheese.core.activity.StubEnv
import coco.cheese.core.callback.IActivity
import coco.cheese.core.engine.javet.ConsoleLogger
import coco.cheese.core.engine.javet.node
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.utils.AssetsUtils
import coco.cheese.core.utils.BaseUtils
import coco.cheese.core.utils.FilesUtils
import coco.cheese.core.utils.HttpUtils
import coco.cheese.core.utils.JS_DIRECTORY
import coco.cheese.core.utils.OsUtils
import coco.cheese.core.utils.UI_DIRECTORY
import coco.cheese.core.utils.WORKING_DIRECTORY
import coco.cheese.core.utils.ZipUtils
import coco.cheese.core.utils.ui.XmlUtils
import com.elvishew.xlog.XLog
import com.hjq.toast.Toaster
import java.lang.ref.WeakReference
import java.net.URL

fun checkUrl(urlStr: String?): Boolean {
    return try {
        val url = URL(urlStr)
        val conn = url.openConnection()
        conn.connectTimeout = 5000
        conn.connect()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

class Utils(private val env: Env) {
    fun run() {
        if (!env.runTime.runMode) {

            env.runTime.runState = true
            HttpUtils.get()
                .getDownload("${env.runTime.ip}/download", "${WORKING_DIRECTORY()}/debug.zip")
            ZipUtils.get().decompress(
                "${WORKING_DIRECTORY()}/debug.zip",
                WORKING_DIRECTORY().toString(), ""
            )
            val node = node()
            node.createNodeRuntime()
            node.run(
                "${JS_DIRECTORY()}/main.js",
                js = FilesUtils.get().read("${JS_DIRECTORY()}/main.js")!!.joinToString("\n")
            )
            env.runTime.runState = false
        } else {
            env.runTime.runState = true

            AssetsUtils.get()
                .copy("release.zip", WORKING_DIRECTORY().toString())
            ZipUtils.get().decompress(
                "${WORKING_DIRECTORY()}/release.zip",
                WORKING_DIRECTORY().toString(), ""
            )
            XmlUtils.get().parseXml("${UI_DIRECTORY()}/activity_main.xml")
            BaseUtils.get().startActivity("Keep", object : IActivity {
                override fun onCreate(stubEnv: StubEnv) {
                    stubEnv.activity.setContentView(stubEnv.view)
                }
            })
            val node = node()
            node.createNodeRuntime()
            node.run(
                "${JS_DIRECTORY()}/main.js",
                js = FilesUtils.get().read("${JS_DIRECTORY()}/main.js")!!.joinToString("\n")
            )
            env.runTime.runState = false
        }
    }

    fun runUi() {
        HttpUtils.get()
            .getDownload("${env.runTime.ip}/download", "${WORKING_DIRECTORY()}/debug.zip")
        ZipUtils.get().decompress(
            "${WORKING_DIRECTORY()}/debug.zip",
            WORKING_DIRECTORY().toString(), ""
        )
        XmlUtils.get().parseXml("${UI_DIRECTORY()}/activity_main.xml")
        BaseUtils.get().startActivity("Keep", object : IActivity {
            override fun onCreate(stubEnv: StubEnv) {
                stubEnv.activity.setContentView(stubEnv.view)
            }
        })
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<Utils>? = null
        private var instance: Utils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): Utils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = Utils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): Utils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(Utils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}