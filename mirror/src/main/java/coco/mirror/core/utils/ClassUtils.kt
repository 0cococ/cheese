package coco.mirror.core.utils

import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.lang.reflect.Method


class ClassUtils(private val env: Env) {

    private lateinit var clz: Class<*>
    private var obj: Any? = null
    private lateinit var method: Method
    private lateinit var field: Field

    fun hasClass(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    fun new(): ClassUtils {
        obj = clz.newInstance()
        return this
    }

    fun new(vararg args: Any?): ClassUtils {
        val constructor = clz.getDeclaredConstructor(*args.toJavaClasses())
        obj = constructor.newInstance(*args)
        return this
    }

    fun getMethod(name: String, vararg args: Any?): ClassUtils {
        method = clz.getMethod(name, *args.toJavaClasses())
        method.isAccessible = true
        return this
    }

    fun getField(name: String): Any? {
        field = clz.getDeclaredField(name)
        field.isAccessible = true
        return field.get(obj)
    }

    fun invoke(vararg args: Any?): Any? {
        return method.invoke(obj, *args)
    }

    fun findClass(className: String): ClassUtils {
        clz = Class.forName(className)
        return this
    }

    private fun Array<out Any?>.toJavaClasses(): Array<Class<*>> {
        return this.map { it?.javaClass ?: Any::class.java }.toTypedArray()
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