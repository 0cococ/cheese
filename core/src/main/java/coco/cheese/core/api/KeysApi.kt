package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IKeysClient
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.KeysUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class KeysApi (private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun homeAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().home()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun home():Boolean {
       return env.invoke<IKeysClient>().home()
    }

    @V8Function
    fun backAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().back()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun back():Boolean {
        return env.invoke<IKeysClient>().back()
    }
    @V8Function
    fun quickSettingsAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().quickSettings()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun quickSettings():Boolean {
        return env.invoke<IKeysClient>().quickSettings()
    }
    @V8Function
    fun powerDialogAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().powerDialog()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun powerDialog():Boolean {
        return env.invoke<IKeysClient>().powerDialog()
    }
    @V8Function
    fun pullNotificationBarAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().pullNotificationBar()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun pullNotificationBar():Boolean {
        return env.invoke<IKeysClient>().pullNotificationBar()
    }
    @V8Function
    fun recentsAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().recents()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun recents():Boolean {
        return env.invoke<IKeysClient>().recents()
    }

    @V8Function
    fun lockScreenAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().lockScreen()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun lockScreen():Boolean {
        return env.invoke<IKeysClient>().lockScreen()
    }

    @V8Function
    fun screenShotAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().screenShot()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun screenShot():Boolean {
        return env.invoke<IKeysClient>().screenShot()
    }

    @V8Function
    fun splitScreenAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<IKeysClient>().splitScreen()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun splitScreen():Boolean {
        return env.invoke<IKeysClient>().splitScreen()
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<KeysApi>? = null
        private var instance: KeysApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : KeysApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = KeysApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): KeysApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(KeysApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}