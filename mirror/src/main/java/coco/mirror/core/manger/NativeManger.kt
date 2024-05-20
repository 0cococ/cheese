package coco.mirror.core.manger


import android.os.Build
import android.text.TextUtils
import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import coco.mirror.core.utils.APP_THAT_LIB_DIRECTORY
import coco.mirror.core.utils.FilesUtils
import coco.mirror.core.utils.ZipUtils
import dalvik.system.DexClassLoader
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.LinkedList

class NativeManger(private val env: Env) {
    fun loadNative(
        zipFilePath: String,
        optimizedDirectory: String,
        appClassLoader: ClassLoader
    ) {
        try {


            val librarySearchPath = runCatching {
                ZipUtils.get().extractLibsFromApk(zipFilePath, optimizedDirectory)
                File(optimizedDirectory).absolutePath
            }.onFailure {
                it.printStackTrace()
            }.getOrNull()


            if (TextUtils.isEmpty(librarySearchPath)) return
            val mLibDir = File(librarySearchPath + File.separator + Build.CPU_ABI)
            val pluginNativeLibraryDirList: MutableList<File> = LinkedList()
            pluginNativeLibraryDirList.add(mLibDir)

            // 获取到DexPathList对象
            val baseDexClassLoaderClass: Class<*> = DexClassLoader::class.java.superclass!!
            val pathListField = baseDexClassLoaderClass.getDeclaredField("pathList")
            pathListField.isAccessible = true
            val dexPathList = pathListField[appClassLoader]

            /**
             * 接下来,合并宿主so,系统so,插件so库
             */
            val dexPathListClass:Class<*> = dexPathList.javaClass
            if (pluginNativeLibraryDirList.isNotEmpty()) {
                // 先创建一个汇总so库的文件夹,收集全部
                val allNativeLibDirList: MutableList<File> = ArrayList()
                // 先添加插件的so库地址
                allNativeLibDirList.addAll(pluginNativeLibraryDirList)
                // 获取到宿主的so库地址
                val nativeLibraryDirectoriesField =
                    dexPathListClass.getDeclaredField("nativeLibraryDirectories")
                nativeLibraryDirectoriesField.isAccessible = true
                val old_nativeLibraryDirectories =
                    nativeLibraryDirectoriesField[dexPathList] as List<File>
                allNativeLibDirList.addAll(old_nativeLibraryDirectories)
                // 获取到system的so库地址
                val systemNativeLibraryDirectoriesField =
                    dexPathListClass.getDeclaredField("systemNativeLibraryDirectories")
                systemNativeLibraryDirectoriesField.isAccessible = true
                val systemNativeLibraryDirectories =
                    systemNativeLibraryDirectoriesField[dexPathList] as List<File>
                allNativeLibDirList.addAll(systemNativeLibraryDirectories)
                val allNativeLibraryPathElements: Array<Any?>?
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    //通过makePathElements获取到c++存放的Element
                    val makePathElementsMethod = dexPathListClass.getDeclaredMethod(
                        "makePathElements",
                        MutableList::class.java,
                        MutableList::class.java,
                        ClassLoader::class.java
                    )
                    makePathElementsMethod.isAccessible = true
                    allNativeLibraryPathElements = makePathElementsMethod.invoke(
                        null,
                        allNativeLibDirList,
                        ArrayList<IOException>(),
                        appClassLoader
                    ) as Array<Any?>
                } else {
                    //android 8.0 以上有所改变, nativeLibraryPathElements = makePathElements(this.systemNativeLibraryDirectories);
                    val makePathElementsMethod = dexPathListClass.getDeclaredMethod(
                        "makePathElements",
                        MutableList::class.java
                    )
                    makePathElementsMethod.isAccessible = true
                    allNativeLibraryPathElements =
                        makePathElementsMethod.invoke(null, allNativeLibDirList) as Array<Any?>
                }
                //将合并宿主和插件的so库，重新设置进去
                val nativeLibraryPathElementsField =
                    dexPathListClass.getDeclaredField("nativeLibraryPathElements")
                nativeLibraryPathElementsField.isAccessible = true
                nativeLibraryPathElementsField[dexPathList] = allNativeLibraryPathElements
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<NativeManger>? = null
        private var instance: NativeManger? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): NativeManger {
            if (instance == null || !examine) {
                synchronized(lock) {
                    instance = NativeManger(env)
                }
            }
            return instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): NativeManger {
            if (instanceWeak?.get() == null || !examine) {
                synchronized(lock) {
                    instanceWeak = WeakReference(NativeManger(env))
                }
            }
            return instanceWeak?.get()!!
        }
    }


}