package coco.cheese.core.utils

import android.inputmethodservice.InputMethodService
import android.os.SystemClock
import android.view.KeyEvent
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.lang.ref.WeakReference

class KeyboardUtils(private val env: Env) {

    fun input(text: String) {
        val currentInputConnection = env.inputMethodService.getCurrentInputConnection()
        currentInputConnection.commitText(text, 0)
    }
    fun delete() {
        try {
            val currentInputConnection = env.inputMethodService.getCurrentInputConnection()
            currentInputConnection?.deleteSurroundingText(Int.MAX_VALUE, Int.MAX_VALUE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun enter() {
        val currentInputConnection = env.inputMethodService.getCurrentInputConnection()
        if (currentInputConnection != null) {
            val uptimeMillis = SystemClock.uptimeMillis()
            var keyEvent = KeyEvent(
                uptimeMillis,
                uptimeMillis,
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_ENTER,
                0
            )
            currentInputConnection.sendKeyEvent(keyEvent)
            keyEvent =
                KeyEvent(uptimeMillis, uptimeMillis, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0)
            currentInputConnection.sendKeyEvent(keyEvent)
        }
    }
    companion object : IBase {
        private var instanceWeak: WeakReference<KeyboardUtils>? = null
        private var instance: KeyboardUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): KeyboardUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = KeyboardUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): KeyboardUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(KeyboardUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}