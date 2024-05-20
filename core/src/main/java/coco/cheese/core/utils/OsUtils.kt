package coco.cheese.core.utils

import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.io.File
import java.lang.ref.WeakReference
var ROOT_DIRECTORY = Env.get().context.getExternalFilesDir("")?.parentFile
//var WORKING_DIRECTORY = File(ROOT_DIRECTORY,if (Env.get().runTime.runMode) "release" else "debug")
//val WORKING_DIRECTORY: File by lazy {
//    val runMode = Env.get().runTime.runMode
//    val mode = if (runMode) "release" else "debug"
//    File(ROOT_DIRECTORY, mode)
//}
var LOG_DIRECTORY = File(ROOT_DIRECTORY,"log")
fun WORKING_DIRECTORY(): File {
    val runMode = Env.get().runTime.runMode
    val mode = if (runMode) "release" else "debug"
    return File(ROOT_DIRECTORY, mode)
}

fun MAIN_DIRECTORY() =
     File(WORKING_DIRECTORY(),"main")

fun UI_DIRECTORY() =
     File(MAIN_DIRECTORY(),"ui")

fun ASSETS_DIRECTORY() =
     File(MAIN_DIRECTORY(),"assets")


fun JS_DIRECTORY() =
     File(MAIN_DIRECTORY(), "js")


fun JAVA_MODULE_DIRECTORY() = File(WORKING_DIRECTORY(),"java")
fun PYTHON_MODULE_DIRECTORY() = File(WORKING_DIRECTORY(),"python")
class OsUtils(private val env: Env) {
    companion object: IBase {
        private var instanceWeak: WeakReference<OsUtils>? = null
        private var instance: OsUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : OsUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = OsUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): OsUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(OsUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }


    }
}