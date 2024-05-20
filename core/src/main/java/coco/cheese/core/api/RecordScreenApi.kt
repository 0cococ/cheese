package coco.cheese.core.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IRecordScreenClient
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class RecordScreenApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun requestPermissionAsync(timeout:Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IRecordScreenClient>().requestPermission(timeout)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun requestPermission(timeout:Int):Boolean {
      return  env.invoke<IRecordScreenClient>().requestPermission(timeout)
    }

    @V8Function
    fun checkPermissionAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IRecordScreenClient>().checkPermission()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun checkPermission(): Boolean {
       return env.invoke<IRecordScreenClient>().checkPermission()
    }

    @V8Function
    fun captureScreenAsync(timeout: Int, x: Int, y: Int, ex: Int, ey: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            val byteArray: ByteArray =env.invoke<IRecordScreenClient>().captureScreen(timeout,x,y,ex,ey)
            Promise(nodeRuntime, task,
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun captureScreen(timeout: Int, x: Int, y: Int, ex: Int, ey: Int): Bitmap? {
        val byteArray: ByteArray =env.invoke<IRecordScreenClient>().captureScreen(timeout,x,y,ex,ey)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size);
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<RecordScreenApi>? = null
        private var instance: RecordScreenApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : RecordScreenApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = RecordScreenApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): RecordScreenApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(RecordScreenApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}