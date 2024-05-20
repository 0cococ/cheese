package coco.cheese.core.engine.javet

import com.caoccao.javet.interception.logging.BaseJavetConsoleInterceptor
import com.caoccao.javet.interop.V8Runtime
import com.caoccao.javet.values.V8Value
import java.text.MessageFormat
import java.util.IllegalFormatException

class JavetStandardConsoleInterceptor(v8Runtime: V8Runtime) :
    BaseJavetConsoleInterceptor(v8Runtime) {
    override fun consoleDebug(vararg v8Values: V8Value?) {
        val firstValue = v8Values[0].toString()
        val formattedMessage= if (firstValue.contains("%")){
            val remainingValues = v8Values.drop(1).toTypedArray()
            String.format(firstValue,*remainingValues)
        }else{
            concat(*v8Values)
        }
        ConsoleLogger.d(formattedMessage)
    }
    override fun consoleError(vararg v8Values: V8Value?) {
        val firstValue = v8Values[0].toString()
        val formattedMessage= if (firstValue.contains("%")){
            val remainingValues = v8Values.drop(1).toTypedArray()
            String.format(firstValue,*remainingValues)
        }else{
            concat(*v8Values)
        }
        ConsoleLogger.e(formattedMessage)
    }
    override fun consoleInfo(vararg v8Values: V8Value?) {
        val firstValue = v8Values[0].toString()
        val formattedMessage= if (firstValue.contains("%")){
            val remainingValues = v8Values.drop(1).toTypedArray()
            String.format(firstValue,*remainingValues)
        }else{
            concat(*v8Values)
        }
        ConsoleLogger.i(formattedMessage)
    }
    override fun consoleLog(vararg v8Values: V8Value) {

        val firstValue = v8Values[0].toString()
        val formattedMessage= if (firstValue.contains("%")){
            val remainingValues = v8Values.drop(1).toTypedArray()
            String.format(firstValue,*remainingValues)
        }else{
            concat(*v8Values)
        }
        ConsoleLogger.i(formattedMessage)

    }
    override fun consoleTrace(vararg v8Values: V8Value?) {
        val firstValue = v8Values[0].toString()
        val formattedMessage= if (firstValue.contains("%")){
            val remainingValues = v8Values.drop(1).toTypedArray()
            String.format(firstValue,*remainingValues)
        }else{
            concat(*v8Values)
        }
        ConsoleLogger.i(formattedMessage)

    }
    override fun consoleWarn(vararg v8Values: V8Value?) {
        val firstValue = v8Values[0].toString()
        val formattedMessage= if (firstValue.contains("%")){
            val remainingValues = v8Values.drop(1).toTypedArray()
            String.format(firstValue,*remainingValues)
        }else{
            concat(*v8Values)
        }
        ConsoleLogger.w(formattedMessage)
    }

}

