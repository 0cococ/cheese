package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ProcessUtils
import coco.cheese.core.utils.ReleaseUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ReleaseApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun  releaseAsync(resource: Any): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ReleaseUtils>().release(resource)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun  release(resource: Any):Boolean {
       return env.invoke<ReleaseUtils>().release(resource)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ReleaseApi>? = null
        private var instance: ReleaseApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ReleaseApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ReleaseApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ReleaseApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ReleaseApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}