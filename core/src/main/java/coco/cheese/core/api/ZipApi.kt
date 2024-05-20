package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.TimeUtils
import coco.cheese.core.utils.ZipUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ZipApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun compressAsync(srcFilePath: String, destFilePath: String, password: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ZipUtils>().compress(srcFilePath,destFilePath,password)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun compress(srcFilePath: String, destFilePath: String, password: String): Boolean {
        return env.invoke<ZipUtils>().compress(srcFilePath,destFilePath,password)
    }


    @V8Function
    fun decompressAsync(zipFilePath: String, destFilePath: String, password: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ZipUtils>().decompress(zipFilePath,destFilePath,password)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun decompress(zipFilePath: String, destFilePath: String, password: String): Boolean {
        return env.invoke<ZipUtils>().decompress(zipFilePath,destFilePath,password)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ZipApi>? = null
        private var instance: ZipApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ZipApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ZipApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ZipApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ZipApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}