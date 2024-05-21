package coco.cheese.plugin.python

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class PythonCore {

    fun start(context: Context){
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
    }

    fun execPy(code: String){
        val py = Python.getInstance()
        val module = py.getModule("exec")
        module.callAttr("execPy",code)

    }

}