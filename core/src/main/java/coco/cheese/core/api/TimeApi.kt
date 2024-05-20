package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.StringUtils
import coco.cheese.core.utils.TimeUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class TimeApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun timeFormatAsync(timestamp: Long, pn: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<TimeUtils>().timeFormat(timestamp,pn)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun timeFormat(timestamp: Long, pn: String): String {
        return env.invoke<TimeUtils>().timeFormat(timestamp,pn)
    }

    @V8Function
    fun getTimeAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<TimeUtils>().getTime()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun getTime(): Long {
        return env.invoke<TimeUtils>().getTime()
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<TimeApi>? = null
        private var instance: TimeApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : TimeApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = TimeApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): TimeApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(TimeApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}