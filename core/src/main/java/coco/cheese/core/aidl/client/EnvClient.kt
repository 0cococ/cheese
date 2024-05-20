package coco.cheese.core.aidl.client

import android.os.IBinder
import android.os.Process
import coco.cheese.core.ui.textList
import coco.cheese.core.utils.BaseUtils
import coco.cheese.core.utils.ProcessUtils
import coco.cheese.core.utils.ui.XmlUtils
import com.elvishew.xlog.XLog

class EnvClient:IEnvClient.Stub() {
    override fun setTextList(str:String) {
        textList.add(str)
    }

    override fun startActivity(activityId: String, viewID: String, callbackID: String) {
        BaseUtils.get().startActivity(activityId,viewID,callbackID)
    }
}