package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IConsoleClient
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ClassUtils
import coco.cheese.core.utils.ConsoleUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ConsoleApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key:String) {
        this.nodeRuntime=env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun showAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IConsoleClient>().show()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun show() {
          env.invoke<IConsoleClient>().show()
    }

    @V8Function
    fun logAsync(log:String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IConsoleClient>().log(log)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun log(log:String) {
        env.invoke<IConsoleClient>().log(log)
    }
    @V8Function
    fun clearAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IConsoleClient>().clear()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun clear() {
        env.invoke<IConsoleClient>().clear()
    }
    @V8Function
    fun hideAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IConsoleClient>().hide()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun hide() {
        env.invoke<IConsoleClient>().hide()
    }
    @V8Function
    fun setTouchAsync(enabled: Boolean): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IConsoleClient>().setTouch(enabled)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun setTouch(enabled: Boolean) {
        env.invoke<IConsoleClient>().setTouch(enabled)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ConsoleApi>? = null
        private var instance: ConsoleApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ConsoleApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ConsoleApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ConsoleApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ConsoleApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}