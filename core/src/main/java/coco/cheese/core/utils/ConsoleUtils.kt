package coco.cheese.core.utils

import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.elvishew.xlog.XLog
import com.hjq.window.EasyWindow
import java.lang.ref.WeakReference
import kotlin.math.max

class ConsoleUtils(private val env: Env) {

    lateinit var textView: AppCompatTextView
    lateinit var view: View
    lateinit var wm: WindowManager
    lateinit var layoutParams: WindowManager.LayoutParams
    var consloeState: Boolean = false
    var num = 1
    fun show() {
        if (consloeState) return
        EasyWindow.with(env.activity).apply {
            setContentView(coco.cheese.core.R.layout.layout_floating_window)
            // 设置成可拖拽的
            setDraggable()
            setGravity(Gravity.CENTER)
            val displayMetrics = DisplayMetrics()
            val windowManager = (ContextCompat.getSystemService(
                Env.get().context,
                WindowManager::class.java
            ))!!
            windowManager.defaultDisplay.getRealMetrics(displayMetrics) // 使用getRealMetrics方法获取真实尺寸
            val screenWidth: Int = displayMetrics.widthPixels
            val screenHeight: Int = displayMetrics.heightPixels
            val targetWidth: Int = (screenWidth * 0.55).toInt() // 设置为屏幕宽度的60%
            val targetHeight: Int = (screenHeight * 0.22).toInt() // 设置为屏幕高度的30%
            setHeight(targetHeight)
            setWidth(targetWidth)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                setWindowType(WindowManager.LayoutParams.TYPE_PHONE)
            }
            setWindowFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            setBitmapFormat(PixelFormat.TRANSLUCENT)
            val textView_: AppCompatTextView =
                findViewById<AppCompatTextView>(coco.cheese.core.R.id.tv_content) as AppCompatTextView
            textView_.movementMethod = ScrollingMovementMethod.getInstance()
            val v = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                4.0f,
                Env.get().context.resources.displayMetrics
            )
            textView_.textSize = v * 1.5f
            textView_.setTextColor(Color.WHITE)
            textView_.setBackgroundColor(Color.argb(0x55, 0X00, 0x00, 0x00))
            setOnClickListener(
                coco.cheese.core.R.id.iv_close,
                EasyWindow.OnClickListener<AppCompatImageView?> { easyWindow: EasyWindow<*>, _: AppCompatImageView? ->
                    easyWindow.cancel()
                })

            wm = this.windowManager
            layoutParams = this.windowParams
            textView = textView_
            view = this.contentView.rootView;


        }.show()
        consloeState = true
    }

    fun log(log: String) {
        if (!::textView.isInitialized && !::wm.isInitialized
            && !::layoutParams.isInitialized && !::view.isInitialized
        ) {
            return

        }

        textView.movementMethod = ScrollingMovementMethod.getInstance()
        textView.setSingleLine(false)
        textView.setHorizontallyScrolling(true)
        textView.setTextColor(Color.GREEN)
        textView.textSize = 10F
        textView.append("$num:$log\n")
        textView.post {
            val layout = textView.layout
            layout?.let {
                val lastLineOffset = it.getLineTop(textView.lineCount) - textView.height
                textView.scrollTo(0, max(lastLineOffset, 0))
            }
        }
        textView.setTextColor(Color.GREEN) // 设置字体颜色为绿色
        textView.textSize = 10f
        wm.updateViewLayout(view, layoutParams)
        num++
    }

    fun clear() {
        if (!::textView.isInitialized && !::wm.isInitialized
            && !::layoutParams.isInitialized && !::view.isInitialized
        ) {
            return

        }
        textView.text = ""
        wm.updateViewLayout(
            view, layoutParams
        )
        num = 1
    }

    fun hide() {
        if (!::textView.isInitialized && !::wm.isInitialized
            && !::layoutParams.isInitialized && !::view.isInitialized
        ) {
            return

        }
        if (view.parent != null) {
            wm.removeView(view)
        }
        consloeState = false
        num = 1
    }

    fun setTouch(enabled: Boolean) {
        if (!::textView.isInitialized && !::wm.isInitialized
            && !::layoutParams.isInitialized && !::view.isInitialized
        ) {
            return

        }
        val flags: Int = if (enabled) {
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        layoutParams.flags = flags
        if (consloeState) {
            wm.updateViewLayout(view, layoutParams)
        }
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ConsoleUtils>? = null
        private var instance: ConsoleUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ConsoleUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ConsoleUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ConsoleUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ConsoleUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}