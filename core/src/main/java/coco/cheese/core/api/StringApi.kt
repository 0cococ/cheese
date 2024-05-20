package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ProcessUtils
import coco.cheese.core.utils.StringUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class StringApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun isPathInStorageAsync(str: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<StringUtils>().isPathInStorage(str)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun isPathInStorage(str: String): Boolean {
       return env.invoke<StringUtils>().isPathInStorage(str)
    }


    @V8Function
    fun getRandomStringAsync(length: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<StringUtils>().getRandomString(length)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun getRandomString(length: Int): String {
        return env.invoke<StringUtils>().getRandomString(length)
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<StringApi>? = null
        private var instance: StringApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : StringApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = StringApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): StringApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(StringApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}