package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.RootUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class RootApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override var nodeRuntime: NodeRuntime = env.nodeRuntime["main"]!!.nodeRuntime

    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }




    @V8Function
    fun execAsync(command: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<RootUtils>().exec(command)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun exec(command: String): String {
       return env.invoke<RootUtils>().exec(command)
    }

    @V8Function
    fun requestPermissionAsync(timeout:Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<RootUtils>().requestPermission(timeout)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun requestPermission(timeout:Int): Boolean {
        return  env.invoke<RootUtils>().requestPermission(timeout)
    }

    @V8Function
    fun checkPermissionAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<RootUtils>().checkPermission()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun checkPermission(): Boolean {
        return  env.invoke<RootUtils>().checkPermission()
    }


    @V8Function
    fun parseEventAsync(str:String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<RootUtils>().parseEvent(str)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun parseEvent(str:String): String {
        return  env.invoke<RootUtils>().parseEvent(str)
    }





    companion object : IBase {
        private var instanceWeak: WeakReference<RootApi>? = null
        private var instance: RootApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): RootApi {
            if (instance == null || !examine) {
                synchronized(lock) {
                    instance = RootApi(env)
                }
            }
            return instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): RootApi {
            if (instanceWeak?.get() == null || !examine) {
                synchronized(lock) {
                    instanceWeak = WeakReference(RootApi(env))
                }
            }
            return instanceWeak?.get()!!
        }

    }

}