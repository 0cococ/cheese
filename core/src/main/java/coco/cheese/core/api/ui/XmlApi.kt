package coco.cheese.core.api.ui

import android.view.View
import coco.cheese.core.Env
import coco.cheese.core.api.AppApi
import coco.cheese.core.callback.IActivity
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AppUtils
import coco.cheese.core.utils.ui.XmlUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class XmlApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override var nodeRuntime: NodeRuntime = env.nodeRuntime["main"]!!.nodeRuntime

    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }


    @V8Function
    fun parseXmlAsync(xml: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<XmlUtils>().parseXml(xml)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun parseXml(xml: String): View {
        return env.invoke<XmlUtils>().parseXml(xml)
    }





    @V8Function
    fun getIDAsync(id: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<XmlUtils>().findR(id)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun getID(id: String): Int {
       return env.invoke<XmlUtils>().getID(id)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<XmlApi>? = null
        private var instance: XmlApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): XmlApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = XmlApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): XmlApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(XmlApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }


}