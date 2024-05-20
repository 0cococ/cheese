package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase

import coco.cheese.core.utils.FloatyUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import com.hjq.window.EasyWindow
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class FloatyApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }
    @V8Function
    fun floatWithAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FloatyUtils>().floatWith()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun floatWith(): EasyWindow<*> {
        return  env.invoke<FloatyUtils>().floatWith()
    }

    @V8Function
    fun requestPermissionAsync(timeout:Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FloatyUtils>().requestPermission(timeout)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun requestPermission(timeout:Int): Boolean {
        return  env.invoke<FloatyUtils>().requestPermission(timeout)
    }

    @V8Function
    fun checkPermissionAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FloatyUtils>().checkPermission()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun checkPermission(): Boolean {
        return  env.invoke<FloatyUtils>().checkPermission()
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<FloatyApi>? = null
        private var instance: FloatyApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : FloatyApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = FloatyApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): FloatyApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(FloatyApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}