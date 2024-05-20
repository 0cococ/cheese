package coco.runui.control

import android.widget.EditText
import android.widget.Spinner
import coco.cheese.core.Env
import coco.runui.all
import coco.runui.background
import coco.runui.height
import coco.runui.id
import coco.runui.orientation
import coco.runui.text
import coco.runui.width
import org.w3c.dom.Node

fun spinner(node: Node): Spinner {
    return Spinner(Env.get().context).apply {
        all(node)
    }
}