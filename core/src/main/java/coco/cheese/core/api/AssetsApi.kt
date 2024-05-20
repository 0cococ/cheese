package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AppUtils
import coco.cheese.core.utils.AssetsUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class AssetsApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override var nodeRuntime:NodeRuntime =env.nodeRuntime["main"]!!.nodeRuntime
    @V8Function
    override fun setNodeRuntime(key:String) {
        this.nodeRuntime=env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun readAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AssetsUtils>().read(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun read(path: String): String {
      return  env.invoke<AssetsUtils>().read(path)
    }

    @V8Function
    fun copyAsync(path: String, destPath: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AssetsUtils>().copy(path,destPath)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun copy(path: String, destPath: String): Boolean {
        return  env.invoke<AssetsUtils>().copy(path,destPath)
    }

    @V8Function
    fun isFolderAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AssetsUtils>().isFolder(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun isFolder(path: String): Boolean {
        return  env.invoke<AssetsUtils>().isFolder(path)
    }
    @V8Function
    fun isFileAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<AssetsUtils>().isFile(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun isFile(path: String): Boolean {
        return  env.invoke<AssetsUtils>().isFile(path)
    }
    companion object : IBase {
        private var instanceWeak: WeakReference<AssetsApi>? = null
        private var instance: AssetsApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : AssetsApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = AssetsApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): AssetsApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(AssetsApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}