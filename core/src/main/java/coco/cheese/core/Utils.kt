package coco.cheese.core

import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Pair
import coco.cheese.core.engine.javet.node
import coco.cheese.core.utils.DeviceUtils
import coco.cheese.core.utils.FilesUtils
import coco.cheese.core.utils.StringUtils
import com.elvishew.xlog.XLog
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import com.yanzhenjie.andserver.annotation.GetMapping
import com.yanzhenjie.andserver.annotation.RequestParam
import com.yanzhenjie.andserver.annotation.RestController
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * # utils
 *
 * Created on 2020/6/11
 * @author Vove
 */
@RestController
class WorkerServer {
    @GetMapping("/create")
    fun start(@RequestParam("path") path: String): String {
        Thread{
            val node = node("${this.javaClass.simpleName}@${StringUtils.get().getRandomString(6)}")
            node.createNodeRuntime()
            XLog.e(path)
            node.run(name=path ,js = FilesUtils.get().read(path)!!.joinToString("\n"))
        }.start()

        return "ok"
    }
}
 infix fun <A, B> A.t(that: B): Pair<A, B> = Pair(this, that)
fun extractPackageName(apkFilePath: String): String? {
    val pm: PackageManager = Env.get().context.packageManager
    val packageInfo = pm.getPackageArchiveInfo(apkFilePath, 0)
    return packageInfo?.packageName
}
fun startServer() {
    val server = AndServer.webServer(Env.get().context)
        .port(8080)
        .timeout(10, TimeUnit.SECONDS)
        .listener(object : Server.ServerListener {
            override fun onStarted() {
//                XLog.i(
//                    "服务器绑定地址:" + DeviceUtils.get().getWifiIP()
//                )
            }

            override fun onStopped() {
            }

            override fun onException(e: Exception) {
                e.printStackTrace()
            }
        })
        .build()
    server.startup()
}

fun sendLogToServer(log: String) {
    try {
        val url = URL("${Env.get().runTime.ip}/log_receiver")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.setDoOutput(true)
        connection.setChunkedStreamingMode(10 * 1024 * 1024)
        connection.setReadTimeout(5000) // 设置读取超时时间为10秒
        val encodedLog = URLEncoder.encode(log, StandardCharsets.UTF_8.toString())
            .replace("+", "%20") // 将加号替换为%20
        val outputStream = connection.outputStream
        outputStream.write(encodedLog.toByteArray(StandardCharsets.UTF_8))
        outputStream.flush()
        outputStream.close()
        val responseCode = connection.getResponseCode()
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readLine()
            reader.close()
            // 在这里可以处理服务器返回的响应数据
            println("Response: $response")
        } else {
            println("Response: " + "no OK")
            // 请求失败，可以处理错误情况
        }
        connection.disconnect()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun launchWithExpHandler(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) = GlobalScope.launch(context + ExceptionHandler, start, block)


val ExceptionHandler by lazy {
    CoroutineExceptionHandler { _, throwable ->
        XLog.e("执行失败： ${throwable.message ?: "$throwable"}")
        throwable.printStackTrace()
    }
}

val mainHandler by lazy {
    Handler(Looper.getMainLooper())
}

fun runOnUi(block: () -> Unit) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        block()
    } else {
        mainHandler.post(block)
    }
}

