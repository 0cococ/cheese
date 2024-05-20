package coco.runui.control

import android.widget.EditText
import coco.cheese.core.Env
import coco.runui.all
import coco.runui.background
import coco.runui.height
import coco.runui.id
import coco.runui.orientation
import coco.runui.text
import coco.runui.width
import coco.runui.hint
import org.w3c.dom.Node

fun editText(node: Node): EditText {
   return EditText(Env.get().context).apply {
       all(node)
    }
}