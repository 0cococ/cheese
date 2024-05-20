package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.TimeUtils
import coco.cheese.core.utils.WorkerUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class WorkerApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }
    @V8Function
    fun createAsync(path: String,paths:String?): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<WorkerUtils>().create(path, paths)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun create(path: String,paths:String?): WorkerUtils {
        return env.invoke<WorkerUtils>().create(path,paths)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<WorkerApi>? = null
        private var instance: WorkerApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : WorkerApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = WorkerApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): WorkerApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(WorkerApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}