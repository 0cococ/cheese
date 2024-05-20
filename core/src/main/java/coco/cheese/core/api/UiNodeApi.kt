package coco.cheese.core.api

import cn.vove7.auto.core.api.findAllWith
import cn.vove7.auto.core.viewfinder.ConditionGroup
import coco.cheese.core.BASE
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IUiNodeClient


import coco.cheese.core.callback.NodeCallBack
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ConvertersUtils

import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.annotations.V8RuntimeSetter
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.reference.V8ValuePromise
import com.elvishew.xlog.XLog
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService


class UiNodeApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime

    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun clearNodeCacheAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().clearNodeCache()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun clearNodeCache(): Boolean {
        return env.invoke<IUiNodeClient>().clearNodeCache()
    }

    @V8Function
    fun forEachNodeAsync(mycallback: NodeCallBack.MyCallback): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            env.javaObjects[BASE]!!["findAllWith"]=
                NodeCallBack().setCallBack(mycallback)
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().findAllWith()
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun forEachNode(mycallback: NodeCallBack.MyCallback1):MutableList<String> {
        env.javaObjects[BASE]!!["findAllWith"]=NodeCallBack().setCallBack(mycallback)
        return env.invoke<IUiNodeClient>().findAllWith()
    }


    @V8Function
    fun clickAsync(node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().click(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun click(node: String): Boolean {
        return env.invoke<IUiNodeClient>().click(node)
    }

    @V8Function
    fun globalLongClickAsync( node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().globalLongClick(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun globalLongClick(node: String): Boolean {
        return env.invoke<IUiNodeClient>().globalLongClick(node)
    }

    @V8Function
    fun globalClickAsync( node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().globalClick(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun globalClick( node: String): Boolean {
        return env.invoke<IUiNodeClient>().globalClick(node)
    }

    @V8Function
    fun longClickAsync( node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().longClick(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun longClick( node: String): Boolean {
        return env.invoke<IUiNodeClient>().longClick(node)
    }

    @V8Function
    fun doubleClickAsync( node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().doubleClick(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun doubleClick( node: String): Boolean {
        return env.invoke<IUiNodeClient>().doubleClick(node)
    }

    @V8Function
    fun tryLongClickAsync( node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().tryLongClick(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }
   
    @V8Function
    fun tryLongClick(node: String): Boolean {
        return env.invoke<IUiNodeClient>().tryLongClick(node)
    }

    @V8Function
    fun tryClickAsync(node: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().tryClick(node)
            )
        }
        return v8ValuePromiseResolver.promise
    }
     
    @V8Function
    fun tryClick(node: String): Boolean {
        return env.invoke<IUiNodeClient>().tryClick(node)
    }

    @V8Function
    fun getAsync(node: String, name: String, args: Array<String>): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(
                nodeRuntime, task,
                env.invoke<IUiNodeClient>().get(node,name,args)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun get(node: String, name: String, args: ArrayList<String>): MutableList<String>? {
        return env.invoke<IUiNodeClient>().get(node,name,(ConvertersUtils.get().arrayToArrayList(args).filterIsInstance<String>().toTypedArray()))
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<UiNodeApi>? = null
        private var instance: UiNodeApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): UiNodeApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = UiNodeApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): UiNodeApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(UiNodeApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }


}