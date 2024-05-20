package coco.cheese.core.utils

import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.elvishew.xlog.XLog
import dalvik.system.DexClassLoader
import dalvik.system.DexFile
import java.lang.ref.WeakReference

class ClassUtils(private val env: Env) {


    private val dexFile = env.context.applicationContext.applicationInfo.sourceDir
    private val dex = DexFile(dexFile)
    private var clz: Class<*>? = null
    private var obj: Any? = null
    fun getClassList(packageName: String): List<String> {
        val classNames = mutableListOf<String>()
        try {
            val entries = dex.entries()
            while (entries.hasMoreElements()) {
                val className = entries.nextElement()
                if (className.startsWith(packageName)) {
                    classNames.add(className)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return classNames
    }


    fun loadDex(path: String) {
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
            val pluginClassLoader =
                DexClassLoader(path, env.context.cacheDir.absolutePath, null, pathClassLoader)
            val pluginPathList = pathListField.get(pluginClassLoader)
            // 获取 plugin DexElements 对象
            val pluginDexElements = dexElementsField.get(pluginPathList) as Array<*>

            // 合并
            val newElements = hostDexElements.javaClass.componentType?.let {
                java.lang.reflect.Array.newInstance(
                    it, hostDexElements.size + pluginDexElements.size
                )
            } as Array<*>
            System.arraycopy(hostDexElements, 0, newElements, 0, hostDexElements.size)
            System.arraycopy(
                pluginDexElements,
                0,
                newElements,
                hostDexElements.size,
                pluginDexElements.size
            )

            // 赋值 host dexElements
            dexElementsField.set(hostPathList, newElements)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hasClass(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }


    fun getObj(): Any? {
        return obj
    }

    fun getClz(): Class<*>? {
        return clz
    }

    fun new(): ClassUtils {
        try {
            this.obj = clz?.newInstance()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return this
    }

    fun new(vararg args: Any?): ClassUtils {
        val type = args.map { it?.javaClass ?: Any::class.java }.toTypedArray()
        val constructor = clz?.getDeclaredConstructor(*type)
//        *args.map { it?.javaClass ?: Any::class.java }
//            .toTypedArray()
//        val constructor = clz.getDeclaredConstructor(String::class.java,Int::class.java)
        this.obj = constructor?.newInstance(*args)
        return this
    }


    fun findClass(className: String): ClassUtils {
        clear()
        return try {
            clz = Class.forName(className)
            this
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }

    fun clear() {
        clz = null
        obj = null
    }

    fun set(nodeName: String): String {
        val clzName = clz?.simpleName ?: throw RuntimeException("clz is null")
        val value = obj ?: clz ?: throw RuntimeException("obj and clz are both null")
        env.nodeRuntime[nodeName]?.nodeRuntime?.globalObject?.set(clzName, value)
        return clzName
    }

    fun set(nodeName: String, name: String): String {
        val clzName = clz?.simpleName ?: throw RuntimeException("clz is null")
        val value = obj ?: clz ?: throw RuntimeException("obj and clz are both null")
        env.nodeRuntime[nodeName]?.nodeRuntime?.globalObject?.set(clzName, value)
        env.nodeRuntime[nodeName]!!.nodeRuntime.getExecutor("var $name = ${clzName}")
            .executeVoid()
        return clzName
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<ClassUtils>? = null
        private var instance: ClassUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ClassUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ClassUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ClassUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ClassUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}