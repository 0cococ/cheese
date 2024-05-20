package coco.mirror.core.manger

import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import coco.mirror.core.utils.FilesUtils
import dalvik.system.DexClassLoader
import java.lang.ref.WeakReference

class DexManger(private val env: Env) {

    fun loadDex(dexFilePath:String){
        try {
            val dexPathListClass = Class.forName("dalvik.system.DexPathList")
            val dexElementsField = dexPathListClass.getDeclaredField("dexElements")
            dexElementsField.isAccessible = true
            val classLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader")
            val pathListField = classLoaderClass.getDeclaredField("pathList")
            pathListField.isAccessible = true

            // 获取 host 类加载器
            val pathClassLoader = env.context.classLoader
            val hostPathList = pathListField.get(pathClassLoader)
            // 获取 host DexElements 对象
            val hostDexElements = dexElementsField.get(hostPathList) as Array<*>

            // 获取 plugin 类加载器
            val pluginClassLoader = DexClassLoader(dexFilePath, env.context.cacheDir.absolutePath, null, pathClassLoader)
            val pluginPathList = pathListField.get(pluginClassLoader)
            // 获取 plugin DexElements 对象
            val pluginDexElements = dexElementsField.get(pluginPathList) as Array<*>

            // 合并
            val newElements = hostDexElements.javaClass.componentType?.let {
                java.lang.reflect.Array.newInstance(
                    it, hostDexElements.size + pluginDexElements.size)
            } as Array<*>
            System.arraycopy(hostDexElements, 0, newElements, 0, hostDexElements.size)
            System.arraycopy(pluginDexElements, 0, newElements, hostDexElements.size, pluginDexElements.size)

            // 赋值 host dexElements
            dexElementsField.set(hostPathList, newElements)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<DexManger>? = null
        private var instance: DexManger? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): DexManger {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = DexManger(env)
                }
            }
            return this.instance!!
        }
        override fun getWeak(env: Env, examine: Boolean): DexManger {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(DexManger(env))
                }
            }
            return this.instanceWeak?.get()!!
        }
    }
}