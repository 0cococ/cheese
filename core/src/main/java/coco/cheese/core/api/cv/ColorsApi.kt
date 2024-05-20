package coco.cheese.core.api.cv

import android.graphics.Bitmap
import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AppUtils
import coco.cheese.core.utils.AssetsUtils
import coco.cheese.core.utils.cv.ColorsUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ColorsApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override var nodeRuntime: NodeRuntime = env.nodeRuntime["main"]!!.nodeRuntime

    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun getRgbAsync(inputImage: Bitmap, x: Int, y: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ColorsUtils>().getRgb(inputImage,x,y)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getRgb(inputImage: Bitmap, x: Int, y: Int): IntArray? {
       return env.invoke<ColorsUtils>().getRgb(inputImage,x,y)
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<ColorsApi>? = null
        private var instance: ColorsApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ColorsApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ColorsApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ColorsApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ColorsApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}