package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.ConsoleUtils
import coco.cheese.core.utils.DeviceUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class DeviceApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime

    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun getIMEIAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getIMEI()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getIMEI(): String {
       return env.invoke<DeviceUtils>().getIMEI()
    }

    @V8Function
    fun supportedOAIDAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().supportedOAID()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun supportedOAID(): Boolean {
        return env.invoke<DeviceUtils>().supportedOAID()
    }

    @V8Function
    fun getOAIDAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getOAID()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getOAID(): String {
        return env.invoke<DeviceUtils>().getOAID()
    }


    @V8Function
    fun getPositionAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getPosition()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getPosition(): String? {
        return env.invoke<DeviceUtils>().getPosition()
    }

    @V8Function
    fun getPublicIPAsync(url:String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getPublicIP(url)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getPublicIP(url:String): String {
        return env.invoke<DeviceUtils>().getPublicIP(url)
    }
    @V8Function
    fun getWifiIPAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getWifiIP()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getWifiIP(): String {
        return env.invoke<DeviceUtils>().getWifiIP()
    }
    @V8Function
    fun getAndroidVersionAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getAndroidVersion()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getAndroidVersion(): String {
        return env.invoke<DeviceUtils>().getAndroidVersion()
    }
    @V8Function
    fun getStatusBarHeightAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getStatusBarHeight()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getStatusBarHeight(): Int {
        return env.invoke<DeviceUtils>().getStatusBarHeight()
    }



    @V8Function
    fun getNavigationBarHeightAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getNavigationBarHeight()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getNavigationBarHeight(): Int {
        return env.invoke<DeviceUtils>().getNavigationBarHeight()
    }

    @V8Function
    fun getScreenHeightAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getScreenHeight()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getScreenHeight(): Int {
        return env.invoke<DeviceUtils>().getScreenHeight()
    }

    @V8Function
    fun getScreenWidthAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getScreenWidth()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getScreenWidth(): Int {
        return env.invoke<DeviceUtils>().getScreenWidth()
    }

    @V8Function
    fun getScreenDpiAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getScreenDpi()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getScreenDpi(): Int {
        return env.invoke<DeviceUtils>().getScreenDpi()
    }

    @V8Function
    fun getTimeAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getCurrentTimestamp()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getTime(): Long {
        return env.invoke<DeviceUtils>().getCurrentTimestamp()
    }

    @V8Function
    fun getClipboardAsync(): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().getClipboard()
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun getClipboard(): String? {
        return env.invoke<DeviceUtils>().getClipboard()
    }

    @V8Function
    fun setClipboardAsync(string: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<DeviceUtils>().setClipboard(string)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun setClipboard(string: String): Boolean {
        return env.invoke<DeviceUtils>().setClipboard(string)
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<DeviceApi>? = null
        private var instance: DeviceApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : DeviceApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = DeviceApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): DeviceApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(DeviceApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}