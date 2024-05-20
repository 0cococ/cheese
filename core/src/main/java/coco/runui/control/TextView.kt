package coco.runui.control

import android.widget.Spinner
import android.widget.TextView
import coco.cheese.core.Env
import coco.runui.all
import coco.runui.background
import coco.runui.height
import coco.runui.id
import coco.runui.orientation
import coco.runui.text
import coco.runui.width
import org.w3c.dom.Node

fun textView(node: Node): TextView {
    return TextView(Env.get().context).apply {
        all(node)
    }
}