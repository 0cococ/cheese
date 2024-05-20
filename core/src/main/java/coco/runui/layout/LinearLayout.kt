package coco.runui.layout

import android.widget.LinearLayout
import coco.cheese.core.Env
import coco.runui.all
import coco.runui.background
import coco.runui.height
import coco.runui.orientation
import coco.runui.width
import coco.runui.id
import coco.runui.text
import org.w3c.dom.Node

fun linearLayout(node: Node): LinearLayout {
    return LinearLayout(Env.get().context).apply {
        all(node)
    }
}