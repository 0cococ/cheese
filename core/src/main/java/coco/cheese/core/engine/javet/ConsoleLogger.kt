package coco.cheese.core.engine.javet


import android.os.Looper
import android.os.Process
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IConsoleClient
import coco.cheese.core.aidl.client.IEnvClient
import coco.cheese.core.sendLogToServer
import coco.cheese.core.ui.textList
import coco.cheese.core.utils.TimeUtils
import com.caoccao.javet.interfaces.IJavetLogger
import com.elvishew.xlog.XLog
import java.sql.Time

//fun refreshLog(level: String, msg: String, err: Throwable) {
//
//    when (level) {
//        "I" -> XLog.tag("log").i(msg)
//        "D" -> XLog.tag("log").d(msg)
//        "W" -> XLog.tag("log").w(msg)
//        "E" -> XLog.tag("log").e(msg)
//        "ER" -> XLog.tag("log").e(msg, err)
//        else -> println("level is unknown")
//    }
//    val timeUtils = TimeUtils.get()
//    val currentTimeStamp = timeUtils.getCurrentTS()
//    val formattedTime = timeUtils.tsToStandardTime(currentTimeStamp, "yyyy-MM-dd HH:mm:ss")
//    val pid = Process.myPid()
//    val strlog = "$formattedTime $pid $level $msg"
//
//    textList.add(strlog)
//}

class ConsoleLogger : IJavetLogger {

    companion object {

        fun i(msg: String) {
            val isUiThread = Looper.myLooper() == Looper.getMainLooper()
            // 默认 debug
            XLog.tag("Js").i(msg)
            val timeUtils = TimeUtils.get()
            val currentTimeStamp = timeUtils.getTime()
            val formattedTime = timeUtils.timeFormat(currentTimeStamp, "yyyy-MM-dd HH:mm:ss")
            val pid = Process.myPid()
            val strlog = "$formattedTime $pid I $msg"
            Env.get().invoke<IConsoleClient>().log(strlog)
            if (Env.get().runTime.runMode) return
            Env.get().invoke<IEnvClient>().setTextList(strlog)
            if(isUiThread){
                Thread{
                    sendLogToServer(strlog)
                }.start()
            }else{
                sendLogToServer(strlog)
            }



        }

        fun d(msg: String) {
            val isUiThread = Looper.myLooper() == Looper.getMainLooper()
            XLog.tag("Js").d(msg)
            val timeUtils = TimeUtils.get()
            val currentTimeStamp =  timeUtils.getTime()
            val formattedTime = timeUtils.timeFormat(currentTimeStamp, "yyyy-MM-dd HH:mm:ss")
            val pid = Process.myPid()
            val strlog = "$formattedTime $pid D $msg"
            Env.get().invoke<IConsoleClient>().log(strlog)
            if (Env.get().runTime.runMode) return
            Env.get().invoke<IEnvClient>().setTextList(strlog)
            if(isUiThread){
                Thread{
                    sendLogToServer(strlog)
                }.start()

            }else{
                sendLogToServer(strlog)
            }
        }

        fun w(msg: String) {
            val isUiThread = Looper.myLooper() == Looper.getMainLooper()
            XLog.tag("Js").w(msg)
            val timeUtils = TimeUtils.get()
            val currentTimeStamp = timeUtils.getTime()
            val formattedTime = timeUtils.timeFormat(currentTimeStamp, "yyyy-MM-dd HH:mm:ss")
            val pid = Process.myPid()
            val strlog = "$formattedTime $pid W $msg"
            Env.get().invoke<IConsoleClient>().log(strlog)
            if (Env.get().runTime.runMode) return
            Env.get().invoke<IEnvClient>().setTextList(strlog)
            if(isUiThread){
                Thread{
                    sendLogToServer(strlog)
                }.start()

            }else{
                sendLogToServer(strlog)
            }
        }

        fun e(msg: String) {
            val isUiThread = Looper.myLooper() == Looper.getMainLooper()
            XLog.tag("Js").e(msg)
            val timeUtils = TimeUtils.get()
            val currentTimeStamp = timeUtils.getTime()
            val formattedTime = timeUtils.timeFormat(currentTimeStamp, "yyyy-MM-dd HH:mm:ss")
            val pid = Process.myPid()
            val strlog = "$formattedTime $pid E $msg"
            Env.get().invoke<IConsoleClient>().log(strlog)
            if (Env.get().runTime.runMode) return
            Env.get().invoke<IEnvClient>().setTextList(strlog)
            if(isUiThread){
                Thread{
                    sendLogToServer(strlog)
                }.start()

            }else{
                sendLogToServer(strlog)
            }
        }

        fun e(msg: String, tr: Throwable) {
            val isUiThread = Looper.myLooper() == Looper.getMainLooper()
            XLog.tag("Js").e(msg,tr)
            val timeUtils = TimeUtils.get()
            val currentTimeStamp = timeUtils.getTime()
            val formattedTime = timeUtils.timeFormat(currentTimeStamp, "yyyy-MM-dd HH:mm:ss")
            val pid = Process.myPid()
            val strlog = "$formattedTime $pid E $msg $tr"
            Env.get().invoke<IConsoleClient>().log(strlog)
            if (Env.get().runTime.runMode) return
            Env.get().invoke<IEnvClient>().setTextList(strlog)
            if(isUiThread){
                Thread{
                    sendLogToServer(strlog)
                }.start()

            }else{
                sendLogToServer(strlog)
            }
        }
    }


    override fun debug(message: String) {
        d(message)
    }

    override fun error(message: String) {
        e(message)
    }

    override fun error(message: String, cause: Throwable) {
        e(message, cause)
    }

    override fun info(message: String) {
        i(message)

    }

    override fun warn(message: String) {

        w(message)

    }
}