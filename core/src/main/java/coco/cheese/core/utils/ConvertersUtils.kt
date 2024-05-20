package coco.cheese.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Pair
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.t
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference

class ConvertersUtils(private val env: Env) {
    fun arrayToArrayList(objectList: List<Any>): Array<Any?> {
        val stringArray = arrayOfNulls<Any>(objectList.size)
        for (i in objectList.indices) {
            val obj: Any = objectList[i]
            stringArray[i] = obj.toString()
        }
        return stringArray
    }
    fun pairArray(vararg pairs: Int): Array<Pair<Int, Int>> {
        require(pairs.size % 2 == 0) { "The number of arguments must be even." }
        val result = Array(pairs.size / 2) { index ->
            pairs[index * 2] t pairs[index * 2 + 1]
        }
        return result
    }
    fun pairArrays(vararg arrays: Array<Pair<Int, Int>>): Array<Array<Pair<Int, Int>>> {
        val result = Array(arrays.size) { index ->
            arrays[index]
        }
        return result
    }
    fun sdToStream(filePath: String): InputStream? {
        return try {
            FileInputStream(filePath)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            null
        }
    }
    fun assetsToStream(fileName: String): InputStream? {
        val newPath = if (fileName.startsWith("/")) {
            fileName.substring(1)
        } else {
            fileName
        }
        return if (!env.runTime.runMode) {
            val filePath = "${ASSETS_DIRECTORY()}/$newPath"
           sdToStream(filePath)
        } else {
            val assetManager = env.context.assets
            assetManager.open(newPath)
        }
    }
    fun streamToBitmap(inputStream: InputStream): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bitmap
    }
    fun bitmapToStream(bitmap: Bitmap): InputStream {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return ByteArrayInputStream(outputStream.toByteArray())
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<ConvertersUtils>? = null
        private var instance: ConvertersUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ConvertersUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ConvertersUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ConvertersUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ConvertersUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}