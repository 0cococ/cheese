package coco.runui

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import coco.cheese.core.Env
import org.w3c.dom.Node

enum class ParamType(val value: String) {
    MATCH_PARENT("match_parent"),
    WRAP_CONTENT("wrap_content"),
    DP("dp"),
    PX("px")
}
fun parseSize(value: String): Int {
    try {
        val numericValue = value.replace("[^\\d]".toRegex(), "")
        val size = numericValue.toInt()
        return when {
            value.contains(ParamType.DP.value) -> dpToPx(size)
            value.contains(ParamType.PX.value) -> size
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return ViewGroup.LayoutParams.WRAP_CONTENT // 默认值
}
fun dpToPx(dp: Int): Int {
    val density: Float = Env.get().context.resources.displayMetrics.density
    return (dp * density).toInt()
}


