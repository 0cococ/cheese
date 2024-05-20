package coco.runui

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import coco.cheese.core.Env
import coco.runui.control.button
import coco.runui.control.editText
import coco.runui.control.spinner
import coco.runui.control.textView
import coco.runui.layout.linearLayout
import com.elvishew.xlog.XLog
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap
import javax.xml.parsers.DocumentBuilderFactory

class ExportApi {
    val viewMap: MutableMap<String, Any?> = ConcurrentHashMap<String, Any>().toMutableMap()
    private lateinit var rootView: ViewGroup
    fun parseXml(instream: InputStream): View {
        val factory = DocumentBuilderFactory.newInstance()
        val dombuilder = factory.newDocumentBuilder()
        val dom: Document = dombuilder.parse(instream)
        return traverseXmlTree(dom.documentElement)
    }

    fun parseXml(xmlString: String): View {
        val factory = DocumentBuilderFactory.newInstance()
        val dombuilder = factory.newDocumentBuilder()
        val inputStream = ByteArrayInputStream(xmlString.toByteArray(Charsets.UTF_8))
        val dom: Document = dombuilder.parse(inputStream)
        return traverseXmlTree(dom.documentElement)
    }
    private fun traverseXmlTree(node: Node): View {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val parentNode = node.parentNode
            if (!::rootView.isInitialized) {
                rootView = android.widget.FrameLayout(Env.get().context)
            }

            val parentView = if (parentNode is Element) {
                viewMap[parentNode.toString()] as? ViewGroup
            } else {
                rootView
            }
            val currentView = if (node.nodeName.contains("Layout")) {
                XLog.e("addLayout ${node.nodeName} to ${parentNode.nodeName} / ${parentNode}")
                parseLayout(node)
            } else {
                XLog.e("addControl ${node.nodeName} to ${parentNode.nodeName} / ${parentNode}")
                parseControl(node)
            }
            parentView?.let { parent ->
                currentView?.let { current ->
                    viewMap[node.toString()] = current
                    parent.addView(current)
                }
            }
        }
        val children = node.childNodes
        for (i in 0 until children.length) {
            traverseXmlTree(children.item(i))
        }
        return rootView

    }
}

fun parseLayout(node: Node): ViewGroup? {
    return when (node.nodeName) {
        "LinearLayout" -> {
            linearLayout(node)
        }
        else -> {
            println("x is something else")
            null
        }
    }
}

fun parseControl(node: Node): View? {
    return when (node.nodeName) {
        "Button" -> {
            button(node)
        }
        "EditText" -> {
         editText(node)
        }
        "Spinner" -> {
            spinner(node)
        }
        "TextView" -> {
            textView(node)
        }
        else -> {
            println("x is something else")
            null
        }
    }
}