package coco.cheese.core.service

import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.auto.core.AppScope
import coco.cheese.core.Env
import com.elvishew.xlog.XLog
import org.koin.java.KoinJavaComponent

class AccessibilityService : AccessibilityApi() {

    //启用 页面更新 回调
    override val enableListenPageUpdate: Boolean = true

    override fun onCreate() {
        //must set

        baseService = this
        KoinJavaComponent.get<Env>(Env::class.java).accessibilityService=this;
        super.onCreate()
    }

    override fun onDestroy() {
        //must set
        baseService = null
        super.onDestroy()
    }

    //页面更新回调
    override fun onPageUpdate(currentScope: AppScope) {
        XLog.tag(TAG).i("onPageUpdate: $currentScope")
    }

    companion object {
        private const val TAG = "MyAccessibilityService"
    }

}