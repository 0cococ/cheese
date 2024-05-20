package coco.cheese.core.api

import android.graphics.Bitmap
import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ConvertersUtils
import coco.cheese.core.utils.YoloUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import com.tencent.yolov8ncnn.Yolov8Ncnn
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class YoloApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun loadYoloAsync(path: String, list: ArrayList<String>, cpugpu: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<YoloUtils>().loadYolo(path,(ConvertersUtils.get().arrayToArrayList(list).filterIsInstance<String>().toTypedArray()),cpugpu)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun loadYolo(path: String, list: ArrayList<String>, cpugpu: Int): Boolean {
        return   env.invoke<YoloUtils>().loadYolo(path, (ConvertersUtils.get().arrayToArrayList(list).filterIsInstance<String>().toTypedArray()),cpugpu)
    }


    @V8Function
    fun detectAsync(bitmap: Bitmap): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<YoloUtils>().detect(bitmap)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun detect(bitmap: Bitmap): Array<Yolov8Ncnn.Obj?> {
        return   env.invoke<YoloUtils>().detect(bitmap)
    }


    @V8Function
    fun getSpeedAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<YoloUtils>().getSpeed()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun getSpeed(): Double {
        return   env.invoke<YoloUtils>().getSpeed()
    }

    @V8Function
    fun drawAsync(objects: Array<Yolov8Ncnn.Obj>?, b: Bitmap): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<YoloUtils>().draw(objects,b)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun draw(objects: Array<Yolov8Ncnn.Obj>?, b: Bitmap): Bitmap {
        return   env.invoke<YoloUtils>().draw(objects,b)
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<YoloApi>? = null
        private var instance: YoloApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : YoloApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = YoloApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): YoloApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(YoloApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}