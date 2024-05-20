package coco.cheese.core.utils

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import android.os.Process
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.lang.ref.WeakReference
import java.util.Timer
import java.util.TimerTask
import kotlin.system.exitProcess


class ProcessUtils(private val env: Env) {

    fun getProcessName(): String? {
        val pid = Process.myPid()
        val am = env.context.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }
    fun isMainProcess(): Boolean {
        return env.context.packageName == getProcessName()
    }
    fun setClassLoader(){
        Thread.currentThread().contextClassLoader = ClassLoader.getSystemClassLoader();
    }
    fun exit(){
        Timer().schedule(object : TimerTask() {
            override fun run() {
//                val intent = Intent(env.context, coco.cheese.core.aidl.Service::class.java)
//                env.context.stopService(intent)
                    Process.killProcess(Process.myPid())
                    exitProcess(1)
            }
        }, 100)
    }



    companion object : IBase {
        private var instanceWeak: WeakReference<ProcessUtils>? = null
        private var instance: ProcessUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ProcessUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ProcessUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ProcessUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ProcessUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }


    }



}
