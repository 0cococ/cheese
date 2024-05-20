package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AppUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.annotations.V8RuntimeSetter
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.Thread.sleep
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService


class AppApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override var nodeRuntime:NodeRuntime =env.nodeRuntime["main"]!!.nodeRuntime
    @V8Function
    override fun setNodeRuntime(key:String) {
       this.nodeRuntime=env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun openUrlAsync(url: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AppUtils>().openUrl(url)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun openUrl(url: String) {
          env.invoke<AppUtils>().openUrl(url)
    }

    @V8Function
    fun uninstallAsync(packageName: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AppUtils>().uninstall(packageName)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun uninstall(packageName: String) {
        env.invoke<AppUtils>().uninstall(packageName)
    }

    @V8Function
    fun getPackageNameAsync(appName: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AppUtils>().getPackageName(appName)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getPackageName(appName: String):String? {
        return env.invoke<AppUtils>().getPackageName(appName)
    }

    @V8Function
    fun getAppNameAsync(packageName: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AppUtils>().getAppName(packageName)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getAppName(packageName: String):String? {
       return env.invoke<AppUtils>().getAppName(packageName)
    }

    @V8Function
    fun openAppSettingsAsync(packageName: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
              env.invoke<AppUtils>().openAppSettings(packageName)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun openAppSettings(packageName: String): Boolean {
        return  env.invoke<AppUtils>().openAppSettings(packageName)
    }

    @V8Function
    fun openAppAsync(packageName: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AppUtils>().openApp(packageName)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun openApp(packageName: String):Boolean {
         return env.invoke<AppUtils>().openApp(packageName)
    }


    @V8Function
    fun openSchemeAsync(schemeUri: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AppUtils>().openScheme(schemeUri=schemeUri)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun openScheme(schemeUri: String): Boolean {
        return  env.invoke<AppUtils>().openScheme(schemeUri=schemeUri)
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<AppApi>? = null
        private var instance: AppApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : AppApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = AppApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): AppApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(AppApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}