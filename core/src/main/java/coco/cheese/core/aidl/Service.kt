package coco.cheese.core.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import coco.cheese.core.Env
import coco.cheese.core.aidl.server.UiNodeService

import coco.cheese.core.aidl.service.ConsoleService
import coco.cheese.core.aidl.service.EnvService
import coco.cheese.core.aidl.service.IConsoleService
import coco.cheese.core.aidl.service.KeyboardService
import coco.cheese.core.aidl.service.KeysService
import coco.cheese.core.aidl.service.PointService
import coco.cheese.core.aidl.service.RecordScreenService
import coco.cheese.core.utils.ProcessUtils


import com.elvishew.xlog.XLog
import kotlin.system.exitProcess


class Service : Service() {

    override fun onCreate() {
        super.onCreate()
        XLog.i("运行进程ID:" + Process.myPid())
//        stopSelf()
    }
    override fun onDestroy() {
        super.onDestroy()
        XLog.i("销毁运行进程ID:" + Process.myPid())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent,flags,startId)
        intent?.let {
            if (intent.hasExtra("ip") && intent.hasExtra("runMode") ) {
                Env.get().runTime.ip= intent.getStringExtra("ip")!!
                Env.get().runTime.runMode= intent.getBooleanExtra("runMode", false)
            }else{
                XLog.e("Service 参数错误")
            }
        } ?: run {
            ProcessUtils.get().exit()
        }

        return START_STICKY;
    }
      class MyIBinder: IMyIBinder.Stub() {
          override fun getConsoleService(): IBinder {
              return ConsoleService().asBinder()
          }

          override fun getKeyboardService(): IBinder {
              return KeyboardService().asBinder()
          }

          override fun getPointService(): IBinder {
             return PointService().asBinder()
          }

          override fun getUiNodeService(): IBinder {
              return UiNodeService().asBinder()
          }

          override fun getEnvService(): IBinder {
              return EnvService().asBinder()
          }
          override fun getKeysService(): IBinder {
             return KeysService().asBinder()
          }

          override fun getRecordScreenService(): IBinder {
             return RecordScreenService().asBinder()
          }
      }


    @Override
    override fun onBind(intent: Intent): IBinder {
       return MyIBinder().asBinder()
    }
}