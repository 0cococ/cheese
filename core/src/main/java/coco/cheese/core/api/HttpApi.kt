package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.HttpUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class HttpApi (private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun getDownloadAsync(url: String, savePath: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<HttpUtils>().getDownload(url,savePath)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getDownload(url: String, savePath: String):Boolean {
       return env.invoke<HttpUtils>().getDownload(url,savePath)
    }

    @V8Function
    fun getAsync(url: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<HttpUtils>().get(url)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun get(url: String): String {
       return env.invoke<HttpUtils>().get(url)
    }

    @V8Function
    fun getAsync(url: String, fileKey: String, file: File): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<HttpUtils>().get(url,fileKey,file)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun get(url: String, fileKey: String, file: File): String {
       return env.invoke<HttpUtils>().get(url,fileKey,file)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<HttpApi>? = null
        private var instance: HttpApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : HttpApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = HttpApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): HttpApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(HttpApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}