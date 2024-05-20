package coco.cheese.core.api

import android.graphics.Bitmap
import android.util.Pair
import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ConsoleUtils
import coco.cheese.core.utils.ConvertersUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ConvertersApi (private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key:String) {
        this.nodeRuntime=env.nodeRuntime[key]!!.nodeRuntime
    }
    @V8Function
    fun arrayToArrayListAsync(objectList: List<String>): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().arrayToArrayList(objectList)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun arrayToArrayList(objectList: List<String>): Array<Any?> {
       return env.invoke<ConvertersUtils>().arrayToArrayList(objectList)
    }

    @V8Function
    fun pairArrayAsync(vararg pairs: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().pairArray(*pairs)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun pairArray(vararg pairs: Int):  Array<Pair<Int, Int>> {
        return env.invoke<ConvertersUtils>().pairArray(*pairs)
    }

    @V8Function
    fun pairArraysAsync(vararg arrays: Array<Pair<Int, Int>>): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().pairArrays(*arrays)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun pairArrays(vararg arrays: Array<Pair<Int, Int>>):  Array<Array<Pair<Int, Int>>> {
        return env.invoke<ConvertersUtils>().pairArrays(*arrays)
    }



    @V8Function
    fun sdToStreamAsync(filePath: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().sdToStream(filePath)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun sdToStream(filePath: String):  InputStream? {
        return env.invoke<ConvertersUtils>().sdToStream(filePath)
    }

    @V8Function
    fun assetsToStreamAsync(filePath: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().assetsToStream(filePath)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun assetsToStream(filePath: String):  InputStream? {
        return env.invoke<ConvertersUtils>().assetsToStream(filePath)
    }

    @V8Function
    fun streamToBitmapAsync(inputStream: InputStream): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().streamToBitmap(inputStream)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun streamToBitmap(inputStream: InputStream):  Bitmap? {
        return env.invoke<ConvertersUtils>().streamToBitmap(inputStream)
    }

    @V8Function
    fun bitmapToStreamAsync(bitmap: Bitmap): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ConvertersUtils>().bitmapToStream(bitmap)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun bitmapToStream(bitmap: Bitmap):  InputStream {
        return env.invoke<ConvertersUtils>().bitmapToStream(bitmap)
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<ConvertersApi>? = null
        private var instance: ConvertersApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ConvertersApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ConvertersApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ConvertersApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ConvertersApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}