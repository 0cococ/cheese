package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.PointUtils
import coco.cheese.core.utils.ProcessUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ProcessApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun getProcessNameAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ProcessUtils>().getProcessName()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun getProcessName():String? {
        return env.invoke<ProcessUtils>().getProcessName()
    }

    @V8Function
    fun isMainProcessAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ProcessUtils>().isMainProcess()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun isMainProcess(): Boolean {
        return env.invoke<ProcessUtils>().isMainProcess()
    }

    @V8Function
    fun setClassLoaderAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ProcessUtils>().setClassLoader()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun setClassLoader() {
         env.invoke<ProcessUtils>().setClassLoader()
    }

    @V8Function
    fun exitAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ProcessUtils>().exit()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun exit() {
        env.invoke<ProcessUtils>().exit()
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<PointApi>? = null
        private var instance: PointApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : PointApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PointApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PointApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PointApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}