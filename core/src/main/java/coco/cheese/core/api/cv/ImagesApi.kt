package coco.cheese.core.api.cv

import android.graphics.Bitmap
import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AssetsUtils
import coco.cheese.core.utils.cv.ColorsUtils
import coco.cheese.core.utils.cv.ImagesUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import org.opencv.core.Mat
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ImagesApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override var nodeRuntime: NodeRuntime = env.nodeRuntime["main"]!!.nodeRuntime

    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun findImgBySiftAsync(inputImage: Bitmap, targetImage: Bitmap, threshold: Double): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ImagesUtils>().findImgBySift(inputImage,targetImage,threshold)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun findImgBySift(inputImage: Bitmap, targetImage: Bitmap, threshold: Double) {
        env.invoke<ImagesUtils>().findImgBySift(inputImage,targetImage,threshold)
    }


    @V8Function
    fun findImgByOBRAsync(inputImage: Bitmap, targetImage: Bitmap, threshold: Double): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ImagesUtils>().findImgByOBR(inputImage,targetImage,threshold)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun findImgByOBR(inputImage: Bitmap, targetImage: Bitmap, threshold: Double) {
        env.invoke<ImagesUtils>().findImgByOBR(inputImage,targetImage,threshold)
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<ImagesApi>? = null
        private var instance: ImagesApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ImagesApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ImagesApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ImagesApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ImagesApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}