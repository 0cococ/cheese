package coco.cheese.core.utils

import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import java.lang.ref.WeakReference
import java.math.BigInteger


class RootUtils(private val env: Env) {


    fun requestPermission(timeout:Int): Boolean {
        return PermissionsUtils.get().requestPermission(ROOT,timeout)
    }

    fun checkPermission(): Boolean {
        return PermissionsUtils.get().checkPermission(ROOT)
    }

    fun parseEvent(str:String): String {
        var cmd=""
        str.lines().forEach { line ->
            if (line.trim().isNotEmpty()){
                cmd+=parseEventString(line)+"\n"
            }

        }
        return cmd
    }

    fun parseEventString(input: String): String {
        if (input.trim().isEmpty()) return ""
        val hexString = input.substringAfter(":").trim()

        return "sendevent ${input.substringBefore(":")} ${hexStringToDecimalString(hexString)}"
    }

    fun hexStringToDecimalString(hexString: String): String {
        return hexString.split(" ").joinToString(" ") { BigInteger(it, 16).toString() }
    }


    fun exec(command: String): String {
        val result = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val inputStream = process.inputStream
            val errorStream = process.errorStream
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach {
                    result.append(it).append("\n")
                }
            }
            errorStream.bufferedReader().useLines { lines ->
                lines.forEach {
                    result.append(it).append("\n")
                }
            }
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                println("Command '$command' failed with exit code $exitCode")
            }
            inputStream.close()
            errorStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result.toString()
    }

    fun findDev(name: String = "input"): String? {
        val inputText = FilesUtils.get().read("/proc/bus/input/devices")!!.joinToString("\n")
        val lines = inputText.lines()
        var isInputDevice = false
        var handlers = ""
        var dev = ""

        for (line in lines) {
            if (line.startsWith("N: Name=\"${name}\"")) {
                isInputDevice = true
            } else if (isInputDevice && line.startsWith("H: Handlers=")) {
                // 获取 Handlers 属性值
                handlers = line.substringAfter("H: Handlers=").trim()
                dev = handlers.split(" ").last()
                break
            }
        }

        return if (isInputDevice) {
            return dev
        } else {
            null
        }
    }

    fun sendEvent(type: Int, code: Int, value: Int): String {
        return exec("sendevent /dev/input/event0 $type $code $value")
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<RootUtils>? = null
        private var instance: RootUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): RootUtils {
            if (instance == null || !examine) {
                synchronized(lock) {
                    instance = RootUtils(env)
                }
            }
            return instance!!
        }


        override fun getWeak(env: Env, examine: Boolean): RootUtils {
            if (instanceWeak?.get() == null || !examine) {
                synchronized(lock) {
                    instanceWeak = WeakReference(RootUtils(env))
                }
            }
            return instanceWeak?.get()!!
        }


    }


}