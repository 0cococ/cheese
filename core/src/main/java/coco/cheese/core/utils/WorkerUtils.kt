package coco.cheese.core.utils

import android.content.Context
import coco.cheese.core.Env
import coco.cheese.core.engine.javet.node
import coco.cheese.core.interfaces.IBase
import com.elvishew.xlog.XLog
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import com.yanzhenjie.andserver.annotation.GetMapping
import com.yanzhenjie.andserver.annotation.RequestParam
import com.yanzhenjie.andserver.annotation.RestController
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class WorkerUtils(private val env: Env) {
    var workerID=""
//    fun create(path: String): WorkerUtils {
////       StringUtils.get().parsePaths()
////        workerID=HttpUtils.get().get("127.0.0.1:8080/create?path=$path")
//      return this
//    }

    fun create(path: String,paths:String?): WorkerUtils {
       val jsPath = if (paths==null){
            path
        }else{
            FilesUtils.get().findFile(File(path),paths)!!.path
        }
        workerID=HttpUtils.get().get("http://127.0.0.1:8080/create?path=$jsPath")
        return this
    }





    companion object : IBase {
        private var instanceWeak: WeakReference<WorkerUtils>? = null
        private var instance: WorkerUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): WorkerUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = WorkerUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): WorkerUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(WorkerUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}