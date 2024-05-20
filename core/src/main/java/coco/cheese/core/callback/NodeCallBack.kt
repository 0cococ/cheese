package coco.cheese.core.callback

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import coco.cheese.core.aidl.type.NodeType
import coco.cheese.core.interfaces.IBase

class NodeCallBack {
    private var myCallback: MyCallback? = null
    private var myCallback1: MyCallback1? = null
    interface MyCallback {
        fun callbackMethod(accessibilityNodeInfoCompat: AccessibilityNodeInfoCompat): Boolean
    }
    interface MyCallback1 {
        fun callbackMethod(nodeType: NodeType): Boolean
    }
    fun getCallBack(): MyCallback? {
        return myCallback
    }
    fun getCallBack1(): MyCallback1? {
        return myCallback1
    }
    fun setCallBack(callback: MyCallback): NodeCallBack {
        myCallback= object : MyCallback {
            override fun callbackMethod(accessibilityNodeInfoCompat: AccessibilityNodeInfoCompat): Boolean {
                return callback.callbackMethod(accessibilityNodeInfoCompat)
            }
        }
        return this
    }
    fun setCallBack(callback: MyCallback1): NodeCallBack {
        myCallback1= object : MyCallback1 {
            override fun callbackMethod(nodeType: NodeType): Boolean {
                return callback.callbackMethod(nodeType)
            }
        }
        return this
    }
}