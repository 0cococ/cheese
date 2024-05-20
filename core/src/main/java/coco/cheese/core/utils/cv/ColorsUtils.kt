package coco.cheese.core.utils.cv

import android.graphics.Bitmap
import android.util.Log
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.utils.YoloUtils
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.lang.ref.WeakReference

class ColorsUtils(private val env: Env) {

    fun getRgb(inputImage: Bitmap, x: Int, y: Int): IntArray? {
        try {
            val mat = Mat()
            // 读取图像
            Utils.bitmapToMat(inputImage, mat, false)

            // 检查坐标是否在图像范围内
            if (x >= 0 && y >= 0 && x < mat.cols() && y < mat.rows()) {
                // 获取指定坐标的像素值
                val pixelValue = mat.get(y, x)

                // 分离 BGR 值
                val b = pixelValue[0].toInt()
                val g = pixelValue[1].toInt()
                val r = pixelValue[2].toInt()

                // 返回 RGB 值数组
                return intArrayOf(r, g, b)
            } else {
                // 坐标超出图像范围，返回空
                return null
            }
        } catch (e: Exception) {
            // 发生异常，记录错误并返回空
            Log.e("ImageUtils", "Error: ${e.message}")
            return null
        }
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ColorsUtils>? = null
        private var instance: ColorsUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ColorsUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ColorsUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ColorsUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ColorsUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }


    }
}