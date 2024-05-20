package coco.cheese


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coco.cheese.bottomnav.BottomNav
import coco.cheese.core.Env
import coco.cheese.core.utils.PermissionsUtils

import coco.cheese.ui.theme.CheeseTheme
import com.elvishew.xlog.XLog
import com.hjq.toast.Toaster
import kotlin.collections.set


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheeseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNav()
                }
            }
        }
        Env.get().context = this
        Env.get().activity = this
        Env.get().activityResultLauncher["1"] =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                PermissionsUtils.get().screenRecord(result.resultCode, result.data)
            }
        PermissionsUtils.get().requestAllPermissions { ->
            XLog.d("MainActivity", "onCreate: 权限申请完成")
            Toaster.show("SDK v0.0.3")
        }
    }





}




@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheeseTheme {
        BottomNav()
    }
}




