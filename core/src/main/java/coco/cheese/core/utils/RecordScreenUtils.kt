package coco.cheese.core.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjectionManager
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.elvishew.xlog.XLog
import java.lang.ref.WeakReference
import java.nio.ByteBuffer


class RecordScreenUtils(val env: Env) {
    var mediaProjectionManager: MediaProjectionManager=
        env.application.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var imageReader: ImageReader = ImageReader.newInstance(
        ScreenUtils.getScreenWidth(),
        ScreenUtils.getScreenHeight(), PixelFormat.RGBA_8888, 1
    )

    init {
        env.recordScreen.mediaProjectionManager= this.mediaProjectionManager
        env.recordScreen.imageReader=this.imageReader
    }

    fun captureScreen(timeout: Int, x: Int, y: Int, ex: Int, ey: Int): Bitmap? {
        val callback: (result: Boolean) -> Unit = { result ->
            if (result) {
                XLog.i("截图成功")
            } else {
                XLog.i("截图失败")
            }
        }
        setUpVirtualDisplay(callback, x, y, ex, ey)
        for (i in 0 until timeout * 10) {
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            if (env.recordScreen.bitmap != null) {
                return env.recordScreen.bitmap
            }
        }
        return null
    }
    fun requestPermission(timeout:Int): Boolean {
        return PermissionsUtils.get().requestPermission(RECORDSCREEN,timeout)
    }
    fun checkPermission(): Boolean {
       return PermissionsUtils.get().checkPermission(RECORDSCREEN)
    }
    fun setUpVirtualDisplay(
        callback: (result:Boolean) -> Unit,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
       env.recordScreen.virtualDisplay = env.recordScreen.mediaProjection!!.createVirtualDisplay(
            "ScreenCapture",
            ScreenUtils.getScreenWidth(),
            ScreenUtils.getScreenHeight(),
            ScreenUtils.getScreenDensityDpi(),
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface, null, null
        )
        Handler(Looper.getMainLooper()).postDelayed({
            val image = imageReader.acquireLatestImage()
            if (image != null) {
                Log.d("~~~", "get image: $image")
                val cropRect = Rect(left, top, right, bottom)
                val saveTask = SaveTask()
                if (cropRect.left != -1 && cropRect.top != -1 && cropRect.width() != -1 && cropRect.height() != -1) {
                    env.recordScreen.bitmap = saveTask.doInBackground(image)?.let {
                        Bitmap.createBitmap(
                            it,
                            cropRect.left,
                            cropRect.top,
                            cropRect.width(),
                            cropRect.height()
                        )
                    }
                } else {
                    env.recordScreen.bitmap = saveTask.doInBackground(image)
                }
                callback(true)
            } else {
                Log.d("~~~", "image == null")
                callback(false)
            }

        },500)

    }


    @Suppress("DEPRECATION")
    class SaveTask : AsyncTask<Image, Void, Bitmap>() {
        @Deprecated("Deprecated in Java")
        public override fun doInBackground(vararg params: Image): Bitmap? {
            if (params.isEmpty()) {
                return null
            }
            val image = params[0]
            val width = image.width
            val height = image.height
            val planes = image.planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            if (!bitmap.isRecycled) {
                return bitmap
            }
            return null
        }



    }






    private object ScreenUtils {

        fun getScreenWidth(): Int {
            return Resources.getSystem().displayMetrics.widthPixels
        }

        fun getScreenHeight(): Int {
            return Resources.getSystem().displayMetrics.heightPixels + DeviceUtils.get()
                .getNavigationBarHeight()
        }

        fun getScreenDensityDpi(): Int {
            return Resources.getSystem().displayMetrics.densityDpi
        }
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<RecordScreenUtils>? = null
        private var instance: RecordScreenUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): RecordScreenUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = RecordScreenUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): RecordScreenUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(RecordScreenUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }


    }


}