package coco.cheese.core.aidl.client

import android.graphics.Rect
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import cn.vove7.auto.core.utils.ViewChildList
import cn.vove7.auto.core.viewfinder.ConditionGroup
import cn.vove7.auto.core.viewnode.ViewNode
import coco.cheese.core.AIDL
import coco.cheese.core.BIND
import coco.cheese.core.Env
import coco.cheese.core.aidl.service.IUiNodeService
import coco.cheese.core.aidl.type.NodeType
import coco.cheese.core.utils.StringUtils
import coco.cheese.core.utils.UiNodeUtils
import com.elvishew.xlog.XLog
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.Objects

class UiNodeClient : IUiNodeClient.Stub() {
    override fun clearNodeCache(): Boolean {
        return UiNodeUtils.get().clearNodeCache()
    }

    override fun findAllWith(): MutableList<String> {
        val stringList = mutableListOf<String>()
        val thisObj = "${this.javaClass.simpleName}@${StringUtils.get().getRandomString(6)}"
        val uiObj = UiNodeUtils.get().findAllWith() as Array<ViewNode>
//        if (uiObj is Array<*> && uiObj.isArrayOf<ViewNode>()){ }

        uiObj.forEach { node ->
            stringList.add("$thisObj~$node")
        }
        Env.get().javaObjects[BIND]!![thisObj] =
            uiObj
        return stringList
    }

    override fun click(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any { it.toString() == nodeName && UiNodeUtils.get().click(it) as Boolean }
    }

    override fun globalLongClick(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any {
            it.toString() == nodeName && UiNodeUtils.get().globalLongClick(it) as Boolean
        }
    }

    override fun globalClick(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any {
            it.toString() == nodeName && UiNodeUtils.get().globalClick(it) as Boolean
        }
    }

    override fun longClick(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any {
            it.toString() == nodeName && UiNodeUtils.get().longClick(it) as Boolean
        }
    }

    override fun doubleClick(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any {
            it.toString() == nodeName && UiNodeUtils.get().doubleClick(it) as Boolean
        }
    }

    override fun tryLongClick(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any {
            it.toString() == nodeName && UiNodeUtils.get().tryLongClick(it) as Boolean
        }
    }

    override fun tryClick(node: String): Boolean {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return false
        val thisObj = Env.get().javaObjects[BIND]?.get(objName) as? Array<ViewNode> ?: return false
        return thisObj.any {
            it.toString() == nodeName &&  UiNodeUtils.get().tryClick(it) as Boolean
        }
    }

    override fun get(node: String, name: String, args: Array<String>): MutableList<String>? {
        val (objName, nodeName) = node.split("~").takeIf { it.size == 2 } ?: return null
        val obj = Env.get().javaObjects[BIND]?.get(objName)

        return when (obj) {
            is Array<*> -> {
                (obj as? Array<ViewNode>)?.find { it.toString() == nodeName }
            }
            is ViewNode -> obj
            is ViewChildList -> {
                (obj as? ViewChildList)?.find { it.toString() == nodeName }
            }
            else -> null
        }?.let { node ->
            val clazz = node.javaClass
            val stringList = mutableListOf<String>()
            val id = "${this.javaClass.simpleName}@${StringUtils.get().getRandomString(6)}"
            try {
                val method: Method?
                val argsList: MutableList<Any> = mutableListOf()
                val arg: Array<Any>
                val result: Any
                when (name) {
                    "appendText" -> {
                        method = clazz.getMethod(name, CharSequence::class.java)
                        argsList.add(args.first())
                    }
                    else -> {
                        method = clazz.getMethod(name)
                    }
                }
                result = if (argsList.isNotEmpty()) {
                    arg = argsList.toTypedArray()
                    method?.invoke(node, *arg)
                } else {
                    method?.invoke(node)
                } ?: return null

                Env.get().javaObjects[BIND]?.set(id, result)
                if (result is Array<*> && result.isArrayOf<ViewNode>()||result is ViewChildList) {
                    if (result is Array<*>) {
                        result.forEach { stringList.add("$id~$it") }
                    } else if (result is ViewChildList) {
                        result.forEach { stringList.add("$id~$it") }
                    }
                } else {
                    stringList.add("$id~$result")
                }
                stringList
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            }
        }
    }



    fun converter(it: AccessibilityNodeInfoCompat): Boolean {
        val nodeType = NodeType()
        return try {
            val textObj: Any? = it.text
            val idObj: Any? = it.viewIdResourceName
            val descObj: Any? = it.contentDescription
            val clzObj: Any? = it.className
            val pkgObj: Any? = it.packageName
            val clickableObj: Any = it.isClickable
            val out = Rect()
            it.getBoundsInScreen(out)
            val boundsObj: Any = out
            if (textObj != null) {
                nodeType._text = textObj.toString()
            } else {
                nodeType._text = null
            }
            if (idObj != null) {
                nodeType._id = idObj.toString()
            } else {
                nodeType._id = null
            }
            if (descObj != null) {
                nodeType._desc = descObj.toString()
            } else {
                nodeType._desc = null
            }
            if (pkgObj != null) {
                nodeType._pkg = pkgObj.toString()
            } else {
                nodeType._pkg = null
            }
            if (clzObj != null) {
                nodeType._clz = clzObj.toString()
            } else {
                nodeType._clz = null
            }
            nodeType._bounds = boundsObj.toString()
            nodeType._isClickable = clickableObj.toString()
            val s =
                Env.get().javaObjects[AIDL.SERVER]!![IUiNodeService::class.simpleName.toString()] as IUiNodeService
            s.callbackMethod(nodeType)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

}