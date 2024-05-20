package coco.cheese.core.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Process
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import coco.cheese.core.BASE
import coco.cheese.core.Env
import coco.cheese.core.R
import coco.cheese.core.STUB
import coco.cheese.core.callback.IActivity
import coco.cheese.core.utils.ProcessUtils
import coco.cheese.core.utils.ui.View_
import com.elvishew.xlog.XLog
import java.util.concurrent.ConcurrentHashMap
data class StubEnv(
    var view: View?,
    var context: Context,
    var activity: Activity,
)
class StubActivity{

    class Keep : Activity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P0 : Activity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.getSimpleName()] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                try {
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                } catch (e: Exception) {
                    XLog.e(e)
                }
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P1 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P2 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P3 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P4 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P5 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P6 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P7 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P8 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
    class P9 : AppCompatActivity() {
        @SuppressLint("SuspiciousIndentation")
        override fun onCreate(savedInstanceState: Bundle?) {
            Env.get().javaObjects[STUB]!![this.javaClass.simpleName] = this
            if (intent != null && intent.hasExtra("viewID") && intent.hasExtra("callbackID")) {
                val view: View? = try {
                    (Env.get().javaObjects[BASE]!![intent.getStringExtra("viewID")] as? View_)?.view
                } catch (e: Exception) {
                    XLog.e("View错误: $e")
                    null
                }
                setTheme(R.style.Theme_Cheese)
                (Env.get().javaObjects[BASE]!![intent.getStringExtra("callbackID")] as IActivity).onCreate(StubEnv(
                    view, this,this) )
                super.onCreate(savedInstanceState)
            }
        }
    }
}