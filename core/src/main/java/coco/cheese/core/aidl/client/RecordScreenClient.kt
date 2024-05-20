package coco.cheese.core.aidl.client

import android.graphics.Bitmap
import coco.cheese.core.Env
import coco.cheese.core.utils.RecordScreenUtils
import java.io.ByteArrayOutputStream

class RecordScreenClient:IRecordScreenClient.Stub() {
    override fun requestPermission(timeout: Int): Boolean {
      return  RecordScreenUtils.get().requestPermission(timeout)
    }

    override fun checkPermission(): Boolean {
        return  RecordScreenUtils.get().checkPermission()
    }

    override fun captureScreen(timeout: Int, x: Int, y: Int, ex: Int, ey: Int): ByteArray {
        val bitmap: Bitmap = RecordScreenUtils.get().captureScreen(timeout,x,y,ex,ey)!!
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        Env.get().recordScreen.bitmap=null
        return  stream.toByteArray()
    }
}