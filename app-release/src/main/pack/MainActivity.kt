package coco.cheese.pack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coco.cheese.core.Env
import coco.cheese.core.aidl.ServiceManager
import coco.cheese.core.utils.PermissionsUtils
import com.elvishew.xlog.XLog
import com.hjq.toast.Toaster


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Env.get().context = this
        Env.get().activity = this
        Env.get().activityResultLauncher["1"] =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                PermissionsUtils.get().screenRecord(result.resultCode, result.data)
            }
        PermissionsUtils.get().requestAllPermissions { ->
            XLog.d("MainActivity", "onCreate: 权限申请完成")
            Toaster.show("SDK v0.0.4")
            val s = ServiceManager()
            s.Bind(true)
        }


    }
}