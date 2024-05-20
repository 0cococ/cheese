package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AppUtils
import coco.cheese.core.utils.AssetsUtils
import coco.cheese.core.utils.ClassUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class ClassApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key:String) {
        this.nodeRuntime=env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun getClassListAsync(packageName: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ClassUtils>().getClassList(packageName)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getClassList(packageName: String): List<String> {
        return  env.invoke<ClassUtils>().getClassList(packageName)
    }

    @V8Function
    fun loadDexAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ClassUtils>().loadDex(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun loadDex(path: String) {
          env.invoke<ClassUtils>().loadDex(path)
    }

    @V8Function
    fun hasClassAsync(className: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ClassUtils>().hasClass(className)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun hasClass(className: String): Boolean {
       return env.invoke<ClassUtils>().hasClass(className)
    }


    @V8Function
    fun findClassAsync(className: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<ClassUtils>().findClass(className)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun findClass(className: String): ClassUtils {
        return env.invoke<ClassUtils>().findClass(className)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ClassApi>? = null
        private var instance: ClassApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ClassApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ClassApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ClassApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ClassApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }



}