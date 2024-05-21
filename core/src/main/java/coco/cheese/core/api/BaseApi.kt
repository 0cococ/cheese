package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.callback.IActivity
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.interfaces.JAction
import coco.cheese.core.utils.BaseUtils
import coco.cheese.core.utils.FloatyUtils
import coco.cheese.core.utils.ui.XmlUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import com.hjq.window.EasyWindow
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class BaseApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun sleepAsync(tim: Long): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().sleep(tim)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun sleep(tim: Long) {
          env.invoke<BaseUtils>().sleep(tim)
    }

    @V8Function
    fun toastAsync(message: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().toast(message)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun toast(message: String) {
        env.invoke<BaseUtils>().toast(message)
    }

    @V8Function
    fun pythonLogAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().pythonLog()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun pythonLog() {
        env.invoke<BaseUtils>().pythonLog()
    }

    @V8Function
    fun toastAsync(format: String, vararg objects: Any): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().toast(format,objects)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun toast(format: String, vararg objects: Any) {
        env.invoke<BaseUtils>().toast(format,objects)
    }

    @V8Function
    fun exitAsync(format: String, vararg objects: Any): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().exit()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun exit() {
        env.invoke<BaseUtils>().exit()
    }

    @V8Function
    fun runJSAsync(nodeName: String,js:String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().runJS(nodeName,js)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun runJS(nodeName: String,js:String):Any {
       return env.invoke<BaseUtils>().runJS(nodeName,js)
    }

    @V8Function
    fun runOnUiAsync(action: JAction): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<BaseUtils>().runOnUi(action)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun runOnUi(action: JAction) {
        env.invoke<BaseUtils>().runOnUi(action)
    }
    @V8Function
    fun startActivityAsync(activityId: String, iActivity: IActivity): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<BaseUtils>().startActivity(activityId, iActivity)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun startActivity(activityId: String, iActivity: IActivity) {
        env.invoke<BaseUtils>().startActivity(activityId, iActivity)
    }
    companion object : IBase {
        private var instanceWeak: WeakReference<BaseApi>? = null
        private var instance: BaseApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : BaseApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = BaseApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): BaseApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(BaseApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}