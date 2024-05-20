package coco.cheese.core.api

import coco.cheese.core.Env
import coco.cheese.core.engine.javet.Promise
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.interfaces.IEngineBase
import coco.cheese.core.utils.AssetsUtils
import coco.cheese.core.utils.DeviceUtils
import coco.cheese.core.utils.FilesUtils
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.values.reference.V8ValuePromise
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService

class FilesApi(private val env: Env): IEngineBase {
    override val executorService: ExecutorService = env.executorService
    override lateinit var nodeRuntime: NodeRuntime
    @V8Function
    override fun setNodeRuntime(key: String) {
        this.nodeRuntime = env.nodeRuntime[key]!!.nodeRuntime
    }

    @V8Function
    fun readAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().read(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun read(path: String): Array<String>? {
        return env.invoke<FilesUtils>().read(path)
    }

    @V8Function
    fun rmAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().rm(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun rm(path: String): Boolean {
        return env.invoke<FilesUtils>().rm(path)
    }

    @V8Function
    fun createAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().create(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun create(path: String): Boolean {
        return env.invoke<FilesUtils>().create(path)
    }

    @V8Function
    fun copyAsync(sourceDirPath: String, destinationDirPath: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().copy(sourceDirPath,destinationDirPath)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun copy(sourceDirPath: String, destinationDirPath: String): Boolean {
        return env.invoke<FilesUtils>().copy(sourceDirPath,destinationDirPath)
    }
    @V8Function
    fun readJsonAsync(filePath: String, keys: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().readJson(filePath,keys)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun readJson(filePath: String, keys: String): String? {
        return env.invoke<FilesUtils>().readJson(filePath,keys)
    }
    @V8Function
    fun isFileAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().isFile(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun isFile(path: String): Boolean {
        return  env.invoke<FilesUtils>().isFile(path)
    }

    @V8Function
    fun isFolderAsync(path: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().isFolder(path)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun isFolder(path: String): Boolean {
        return  env.invoke<FilesUtils>().isFolder(path)
    }

    @V8Function
    fun appendAsync(filePath: String, content: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().append(filePath,content)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun append(filePath: String, content: String): Boolean {
        return  env.invoke<FilesUtils>().append(filePath,content)
    }

    @V8Function
    fun writeAsync(filePath: String, content: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().write(filePath,content)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun write(filePath: String, content: String): Boolean {
        return  env.invoke<FilesUtils>().write(filePath,content)
    }
    @V8Function
    fun saveAsync(obj: Any, filePath: String): V8ValuePromise {
        val v8ValuePromiseResolver = nodeRuntime.createV8ValuePromise()
        val task: Promise.Task =
            Promise.Task(v8ValuePromiseResolver, "", System.currentTimeMillis())
        executorService.submit {
            Promise(nodeRuntime, task,
                env.invoke<FilesUtils>().save(obj,filePath)
            )
        }
        return v8ValuePromiseResolver.promise
    }
    @V8Function
    fun save(obj: Any, filePath: String): Boolean {
        return  env.invoke<FilesUtils>().save(obj,filePath)
    }




    companion object : IBase {
        private var instanceWeak: WeakReference<FilesApi>? = null
        private var instance: FilesApi? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : FilesApi {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = FilesApi(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): FilesApi {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(FilesApi(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}