package coco.cheese.core.utils

import cn.vove7.andro_accessibility_api.requireBaseAccessibility
import cn.vove7.auto.core.api.findAllWith
import cn.vove7.auto.core.viewfinder.ConditionGroup
import cn.vove7.auto.core.viewnode.ViewNode
import coco.cheese.core.Action
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.utils.model.accessibility.AndroidPlatformReflectionUtils
import java.lang.ref.WeakReference

class UiNodeUtils(private val env: Env) {


    fun clearNodeCache(): Boolean {
        return AndroidPlatformReflectionUtils.clearAccessibilityCache()
    }

    class _findAllWith : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            return try {
                findAllWith { coco.cheese.core.aidl.client.UiNodeClient().converter(it) }
            } catch (e: NumberFormatException) {
                println("查找错误")
                false
            }
        }
    }


    fun findAllWith(vararg points: Any): Any? {
        return Action.runAction(_findAllWith(), *points)
    }

    class _click : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters
            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.click()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }
        }
    }

    fun click(vararg points: Any): Any? {
        return Action.runAction(_click(), *points)
    }

    class _globalLongClick : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters
            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.globalLongClick()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }
        }
    }

    fun globalLongClick(vararg points: Any): Any? {
        return Action.runAction(_globalLongClick(), *points)
    }

    class _globalClick : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters

            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.globalClick()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }
        }
    }

    fun globalClick(vararg points: Any): Any? {
        return Action.runAction(_globalClick(), *points)
    }

    class _longClick : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters
            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.longClick()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }

        }
    }

    fun longClick(vararg points: Any): Any? {
        return Action.runAction(_longClick(), *points)
    }

    class _doubleClick : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters

            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.doubleClick()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }
        }
    }

    fun doubleClick(vararg points: Any): Any? {
        return Action.runAction(_doubleClick(), *points)
    }

    class _tryLongClick : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters
            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.tryLongClick()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }
        }
    }

    fun tryLongClick(vararg points: Any): Any? {
        return Action.runAction(_tryLongClick(), *points)
    }

    class _tryClick : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.isNotEmpty()) { "传入参数数量错误" }
            val (a) = parameters
            return try {
                val node = when (val node = a) {
                    is ConditionGroup -> node
                    is ViewNode -> node
                    else -> throw ClassCastException("a is neither a ConditionGroup nor a ViewNode")
                }
                node.tryClick()
            } catch (e: ClassCastException) {
                println("类型错误: ${e.message}")
                false
            }
        }
    }

    fun tryClick(vararg points: Any): Any? {
        return Action.runAction(_tryClick(), *points)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<UiNodeUtils>? = null
        private var instance: UiNodeUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): UiNodeUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = UiNodeUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): UiNodeUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(UiNodeUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}