package coco.cheese.core.api

import cn.vove7.auto.core.viewfinder.ConditionGroup
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IPointClient
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase

import coco.cheese.core.utils.PointUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class PointApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun swipeToPointAsync(sx: Int, sy: Int, ex: Int, ey: Int, dur: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IPointClient>().swipeToPoint(sx, sy, ex, ey, dur)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun swipeToPoint(sx: Int, sy: Int, ex: Int, ey: Int, dur: Int):Boolean {
        return env.invoke<IPointClient>().swipeToPoint(sx, sy, ex, ey, dur)
    }

    @V8Function
    fun clickPointAsync(sx: Int, sy: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IPointClient>().clickPoint(sx, sy)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun clickPoint(sx: Int, sy: Int):Boolean {
        return env.invoke<IPointClient>().clickPoint(sx, sy)
    }

    @V8Function
    fun longClickPointAsync(sx: Int, sy: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IPointClient>().longClickPoint(sx,sy)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun longClickPoint(sx: Int, sy: Int):Boolean {
        return env.invoke<IPointClient>().longClickPoint(sx,sy)
    }

    @V8Function
    fun touchDownAsync(sx: Int, sy: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IPointClient>().touchDown(sx,sy)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun touchDown(sx: Int, sy: Int):Boolean {
        return env.invoke<IPointClient>().touchDown(sx,sy)
    }

    @V8Function
    fun touchMoveAsync(sx: Int, sy: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IPointClient>().touchMove(sx,sy)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun touchMove(sx: Int, sy: Int):Boolean {
        return env.invoke<IPointClient>().touchMove(sx,sy)
    }

    @V8Function
    fun touchUpAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IPointClient>().touchUp()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun touchUp():Boolean {
        return env.invoke<IPointClient>().touchUp()
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<PointApi>? = null
        private var instance: PointApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : PointApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PointApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PointApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PointApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}