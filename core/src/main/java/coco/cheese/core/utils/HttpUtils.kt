package coco.cheese.core.utils

import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference


class HttpUtils(private val env: Env) {

    private val client = OkHttpClient()
    fun getDownload(url: String, savePath: String): Boolean{

        val request = Request.Builder()
            .url(url)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                // 检查响应是否成功
                if (!response.isSuccessful) {
                    println("Failed to download file. Response Code: ${response.code}")
                    return false
                }

                // 获取响应体
                val responseBody = response.body ?: run {
                    println("Response body is null.")
                    return false
                }
                // 保存文件
                savePath.let { path ->
                    FileOutputStream(File(path)).use { fileOutputStream ->
                        responseBody.byteStream().copyTo(fileOutputStream)
                        println("File downloaded successfully.")
                    }
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
    @Throws(IOException::class)
    fun get(url: String): String {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()
            client.newCall(request).execute().use { response ->
                return response.body?.string() ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }


    }


    fun get(url: String, fileKey: String, file: File): String {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(fileKey, file.name, RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file))
            .build()
        val request = Request.Builder()
            .url(url)
            .method("GET", requestBody)
            .build()
        client.newCall(request).execute().use { response ->
            return response.body?.string() ?: ""
        }
    }

//    fun post(url: String, formData: FormBody, fileKey: String, file: File): String {
//        val fileRequestBody = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file)
//        val requestBody: RequestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addPart(formData)
//            .addFormDataPart(fileKey, file.name, fileRequestBody)
//            .build()
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .build()
//        client.newCall(request).execute().use { response ->
//            return response.body?.string() ?: ""
//        }
//    }



    companion object : IBase {
        private var instanceWeak: WeakReference<HttpUtils>? = null
        private var instance: HttpUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): HttpUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = HttpUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): HttpUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(HttpUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}