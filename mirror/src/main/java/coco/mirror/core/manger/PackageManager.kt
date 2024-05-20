package coco.mirror.core.manger

import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import coco.mirror.core.utils.APP_DIRECTORY
import coco.mirror.core.utils.APP_THAT_DIRECTORY
import coco.mirror.core.utils.APP_THAT_LIB_DIRECTORY
import coco.mirror.core.utils.APP_THAT_THIS
import coco.mirror.core.utils.FilesUtils
import coco.mirror.core.utils.PackageUtils
import java.io.File
import java.lang.ref.WeakReference

class PackageManager(private val env: Env) {

    fun install(path: String){
        val pkg=PackageUtils.get().extractPackageName(path)
        APP_THAT_DIRECTORY = File(pkg!!)
        println(APP_THAT_DIRECTORY)
        if (!FilesUtils.get().isFile(APP_THAT_THIS.path)){
            println("目录不存在重新初始")
            FilesUtils.get().copyFiles(
                path,
                APP_THAT_THIS.path
            )
        }
        env.app.`package` = pkg
        env.app.path = APP_THAT_THIS.path
    }

    fun load(){
        NativeManger.get().loadNative(
            Env.get().app.path,
            APP_THAT_LIB_DIRECTORY.path,
            env.context.classLoader
        )
        DexManger.get().loadDex(
            env.app.path
        )
        ResourcesManger.get().loadRes(
            env.app.path
        )
    }

    fun uninstall(){
        APP_THAT_DIRECTORY.deleteRecursively()
    }



    fun getInstallPackage(): MutableList<String> {
        val directory = File(APP_DIRECTORY.path)
        val files = directory.listFiles()
        val fileNames = mutableListOf<String>()
        files?.forEach { file ->
            if (file.isDirectory) {
                fileNames.add(file.name)
            }
        }

        return fileNames
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<PackageManager>? = null
        private var instance: PackageManager? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): PackageManager {
            if (instance == null || !examine) {
                synchronized(lock) {
                    instance = PackageManager(env)
                }
            }
            return instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PackageManager {
            if (instanceWeak?.get() == null || !examine) {
                synchronized(lock) {
                    instanceWeak = WeakReference(PackageManager(env))
                }
            }
            return instanceWeak?.get()!!
        }
    }
}