package coco.runui.control

import android.widget.Button
import coco.cheese.core.Env
import coco.runui.background
import coco.runui.height
import coco.runui.id
import coco.runui.orientation
import coco.runui.text
import coco.runui.all
import org.w3c.dom.Node

fun button(node: Node): Button {
   return Button(Env.get().context).apply {
       all(node)
    }
}