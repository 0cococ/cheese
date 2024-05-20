package coco.cheese.core.api

import cn.vove7.auto.core.viewfinder.ConditionGroup
import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase

import coco.cheese.core.utils.PermissionsUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class PermissionsApi(private val env: Env) : IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun requestPermissionAsync(permission: Int,timeout:Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<PermissionsUtils>().requestPermission(permission,timeout)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun requestPermission(permission: Int,timeout:Int):Boolean {
        return env.invoke<PermissionsUtils>().requestPermission(permission,timeout)
    }


    @V8Function
    fun checkPermissionAsync(permission: Int): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<PermissionsUtils>().checkPermission(permission)
            )
        }
        return v8ValuePromiseResolver.promise
    }

    @V8Function
    fun checkPermission(permission: Int):Boolean {
        return env.invoke<PermissionsUtils>().checkPermission(permission)
    }

    val _ACCESSIBILITY: Int
        get() {
            return coco.cheese.core.utils.ACCESSIBILITY
        }
    val _FLOAT: Int
        get() {
            return coco.cheese.core.utils.FLOAT
        }

    val _RECORDSCREEN: Int
        get() {
            return coco.cheese.core.utils.RECORDSCREEN
        }


    companion object : IBase {
        private var instanceWeak: WeakReference<PermissionsApi>? = null
        private var instance: PermissionsApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : PermissionsApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PermissionsApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PermissionsApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PermissionsApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}