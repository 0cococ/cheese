package coco.cheese.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coco.cheese.core.utils.ACCESSIBILITY
import coco.cheese.core.utils.ConsoleUtils
import coco.cheese.core.utils.FLOAT
import coco.cheese.core.utils.PermissionsUtils
import com.hjq.toast.Toaster


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Composable
fun PermissionsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("偏我来时不逢春")
                },

                )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {

               if(!PermissionsUtils.get().checkPermission(ACCESSIBILITY)) {
                   PermissionsUtils.get().requestPermission(ACCESSIBILITY,5)
               }else{
                   Toaster.show("无障碍权限正常")
               }

            }) {
                Text("无障碍权限")
            }
            Button(onClick = {
                if(!PermissionsUtils.get().checkPermission(FLOAT)) {
                    PermissionsUtils.get().requestPermission(FLOAT,5)
                }else{
                    ConsoleUtils.get().show()
                    Toaster.show("悬浮窗权限正常")
                }

            }) {
                Text("日志悬浮窗")
            }
        }



    }
}
