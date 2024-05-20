/*
 * Author: coco.
 * Created Date: 2024-05-19.
 * Copyright (c) 2024 coco.
 *
 * Licensed under the GPL-3.0 License.
 * You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package coco.cheese.core

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.display.VirtualDisplay
import android.inputmethodservice.InputMethodService
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import coco.cheese.core.engine.javet.ConsoleLogger
import coco.cheese.core.utils.ClassUtils
import coco.cheese.core.utils.FilesUtils
import coco.cheese.core.utils.OsUtils
import coco.cheese.core.utils.StringUtils
import coco.cheese.core.utils.WORKING_DIRECTORY
import com.caoccao.javet.interop.NodeRuntime
import com.elvishew.xlog.XLog
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.get
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

abstract class Action {
    abstract suspend fun run(vararg parameters: Any): Any?

    companion object {
        fun runAction(action: Action, vararg parameters: Any): Any? {
            return runBlocking {
                return@runBlocking action.run(*parameters)
            }
        }
    }

}

object AIDL {
    val CLIENT = StringUtils.get().getRandomString(10)
    val SERVER = StringUtils.get().getRandomString(10)

}

val BASE = StringUtils.get().getRandomString(10)
val BIND = StringUtils.get().getRandomString(10)
val UTILS = StringUtils.get().getRandomString(10)
val STUB = StringUtils.get().getRandomString(10)

object SERVER_CLASS {
    val AIDL = coco.cheese.core.aidl.Service::class.java.name.toString()
}

class Env {
    lateinit var context: Context
    lateinit var applicationContext: Context
    lateinit var application: Application
    lateinit var appCompatActivity: AppCompatActivity
    lateinit var componentActivity: ComponentActivity
    lateinit var activity: Activity
    lateinit var accessibilityService: AccessibilityService
    lateinit var inputMethodService: InputMethodService
    lateinit var executorService: ExecutorService
    lateinit var runTime: RunTimeNest
    lateinit var recordScreen: RecordScreenNest
    lateinit var javaObjects: MutableMap<Any, MutableMap<String, Any>>
    lateinit var jsObjects: MutableMap<String, Any>
    lateinit var nodeRuntime: MutableMap<String, NodeRuntimeNest>
    lateinit var aidlEnv: MutableMap<String, Env>
    lateinit var activityResultLauncher: MutableMap<String, ActivityResultLauncher<*>>
    lateinit var settings: MutableMap<String, Any>

    data class RecordScreenNest(
        var mediaProjectionManager: MediaProjectionManager? = null,
        var mediaProjection: MediaProjection? = null,
        var virtualDisplay: VirtualDisplay? = null,
        var imageReader: ImageReader? = null,
        var bitmap: Bitmap? = null,
    )

    data class RunTimeNest(
        var ip: String = "127.0.0.1:8080",
        var connect: Boolean = false,
        var runMode: Boolean = false,
        var runState: Boolean = false
    )

    data class NodeRuntimeNest(
        val nodeRuntime: NodeRuntime,
        val logger: ConsoleLogger,
        val daemonRunning: AtomicBoolean,
        val gcScheduled: AtomicBoolean,
        val daemonThread: Thread
    )

    fun forEachClass(
        packageName: String,
        examine: Boolean = true,
        block: (clz: Class<*>, result: Any) -> Unit
    ) {
        ClassUtils.get().getClassList(packageName).forEach { item ->
            if (!item.contains("$")) {
                try {
                    val clazz = Class.forName(item)
                    val clazz1 = Class.forName(item + "\$Companion")
                    val companionField = clazz.getDeclaredField("Companion")
                    companionField.isAccessible = true
                    val method =
                        clazz1.getDeclaredMethod("get", Env::class.java, Boolean::class.java)
                    method.isAccessible = true
                    val result = method.invoke(companionField.get(null), get(), examine)
                    result?.let { block(clazz, it) }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

            }
        }
    }

    inline fun <reified T> invoke(): T {
        return if (!T::class.simpleName!!.contains("Client")) {
            get().javaObjects[UTILS]!![T::class.simpleName] as T
        } else {
            get().javaObjects[AIDL.CLIENT]!![T::class.simpleName.toString()] as T
        }
    }


    companion object {
        private var envRef: WeakReference<Env>? = null
        private var env: Env? = null

        private val lock = Any()


        //        val packageManager = this.packageManager
//        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
//        val activities = packageInfo.activities
//        activities?.forEach { activityInfo ->
//            Log.d("ClassName", activityInfo.name)
//        }
        @JvmOverloads
        fun get(examine: Boolean = true): Env {
            if (this.env == null || !examine) {
                synchronized(this.lock) {
                    this.env = get<Env>(Env::class.java)
                }
            }

            return this.env!!

        }

        @JvmOverloads
        fun getWeak(examine: Boolean = true): Env {
            if (this.envRef?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.envRef = WeakReference(get<Env>(Env::class.java))
                }
            }

            return this.envRef?.get()!!

        }


        fun init(boolean: Boolean,application: Application) {
            get().application = application
            get().applicationContext = application.applicationContext
            get().context=application.applicationContext
            get().executorService = Executors.newFixedThreadPool(999)
            get().runTime = RunTimeNest()
            get().recordScreen = RecordScreenNest()
            get().activityResultLauncher =
                ConcurrentHashMap<String, ActivityResultLauncher<*>>().toMutableMap()
            get().jsObjects = ConcurrentHashMap<String, Any>().toMutableMap()
            get().javaObjects =
                ConcurrentHashMap<Any, MutableMap<String, Any>>().toMutableMap().apply {
                    put(AIDL.CLIENT, mutableMapOf())
                    put(AIDL.SERVER, mutableMapOf())
                    put(UTILS, mutableMapOf())
                    put(BIND, mutableMapOf())
                    put(BASE, mutableMapOf())
                    put(STUB, mutableMapOf())

                }

            get().settings = ConcurrentHashMap<String, Any>().toMutableMap()
            get().nodeRuntime = ConcurrentHashMap<String, NodeRuntimeNest>().toMutableMap()
            get().aidlEnv = ConcurrentHashMap<String, Env>().toMutableMap()
            // 初始化所有工具类
            get().forEachClass("coco.cheese.core.utils") { clz, result ->
                get().javaObjects[UTILS]!![clz.simpleName] = result
            }
            get().runTime.runMode=boolean
            FilesUtils.get().create(WORKING_DIRECTORY().path)

//            ClassUtils.get().getClassList("coco.cheese.core.utils").forEach { item ->
//                if (!item.contains("$")) {
//                    val clazz = Class.forName(item)
//                    val clazz1 = Class.forName(item + "\$Companion")
//                    val companionField = clazz.getDeclaredField("Companion")
//                    companionField.isAccessible = true
//                    val method =
//                        clazz1.getDeclaredMethod("get", Env::class.java, Boolean::class.java)
//                    method.isAccessible = true
//                    val result = method.invoke(companionField.get(null), get(), true)
//                    get().javaObjectsNest[UTILS_KEY.JAVAOBJECTSNEST_UTILS_CLIENT]!![item]=result!!
//                }
//            }


            // 初始化所有暴露方法类
//                val apiMap = mutableMapOf<String, Any>()
//                ClassUtils.get().getClassList("coco.cheese.core.api").forEach { item ->
//                    if (!item.contains("$")) {
//                        val clazz = Class.forName(item)
//                        val clazz1 = Class.forName(item + "\$Companion")
//                        val companionField = clazz.getDeclaredField("Companion")
//                        companionField.isAccessible = true
//                        val method =
//                            clazz1.getDeclaredMethod("get", Env::class.java, Boolean::class.java)
//                        method.isAccessible = true
//                        val result = method.invoke(companionField.get(null), get(), true)
//                        apiMap[item]= result!!
//                    }
//                }
//                get().javaObjectsNest["apiMap"] =apiMap


        }
    }
}