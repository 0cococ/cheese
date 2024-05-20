package coco.cheese.core.aidl


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import coco.cheese.core.AIDL
import coco.cheese.core.Env
import coco.cheese.core.SERVER_CLASS
import coco.cheese.core.aidl.client.ConsoleClient

import coco.cheese.core.aidl.client.EnvClient
import coco.cheese.core.aidl.client.KeyboardClient
import coco.cheese.core.aidl.client.KeysClient

import coco.cheese.core.aidl.client.PointClient
import coco.cheese.core.aidl.client.RecordScreenClient
import coco.cheese.core.aidl.client.UiNodeClient
import coco.cheese.core.aidl.service.IConsoleService
import coco.cheese.core.aidl.service.IEnvService
import coco.cheese.core.aidl.service.IKeyboardService
import coco.cheese.core.aidl.service.IKeysService

import coco.cheese.core.aidl.service.IPointService
import coco.cheese.core.aidl.service.IRecordScreenService
import coco.cheese.core.aidl.service.IUiNodeService
import com.elvishew.xlog.XLog
import java.lang.Thread.sleep


class ServiceManager {
    var isBind = false
    var isConnected = false
    lateinit var serviceConnection: ServiceConnection
    lateinit var iMyIBinder: IMyIBinder
    private fun isAidlInterfaceAlive(binder: IBinder?): Boolean {
        return binder != null && binder.isBinderAlive
    }

    private fun connectToService(guard: Boolean = false) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                isConnected = true
                iMyIBinder = IMyIBinder.Stub.asInterface(service)

                val consoleService = IConsoleService.Stub.asInterface(iMyIBinder.consoleService)
                consoleService.register(ConsoleClient())

                val keyboardService = IKeyboardService.Stub.asInterface(iMyIBinder.keyboardService)
                keyboardService.register(KeyboardClient())

                val envService = IEnvService.Stub.asInterface(iMyIBinder.envService)
                envService.register(EnvClient())

                val keysService = IKeysService.Stub.asInterface(iMyIBinder.keysService)
                keysService.register(KeysClient())

                val nodeService = IUiNodeService.Stub.asInterface(iMyIBinder.uiNodeService)
                nodeService.register(UiNodeClient())

                val pointService = IPointService.Stub.asInterface(iMyIBinder.pointService)
                pointService.register(PointClient())

                val recordScreenService = IRecordScreenService.Stub.asInterface(iMyIBinder.recordScreenService)
                recordScreenService.register(RecordScreenClient())


                Env.get().javaObjects[AIDL.SERVER]!![IUiNodeService::class.simpleName.toString()] =
                    nodeService

                Env.get().javaObjects[AIDL.SERVER]!![IEnvService::class.simpleName.toString()] =
                    envService

                sleep(500)
                try {
                    envService.start()
                    XLog.i("运行进程成功🤝")
                } catch (e: RemoteException) {
                    XLog.i("运行进程已死亡")
                    Unbind()
                    if (guard) {
                        XLog.i("尝试重启运行进程")
                        Bind(guard)
                        XLog.i("重启运行进程完毕")
                    }

                }

            }

            override fun onServiceDisconnected(name: ComponentName) {
                isConnected = false
            }
        }
    }

    fun Bind(guard: Boolean = false) {
        if (guard) {
            XLog.i("开启运行进程守护")
        }

        connectToService(guard)
        if (isConnected && isBind) {
            XLog.i("已启动运行进程")
        } else {
            XLog.i("启动运行进程")
            val componentName = ComponentName(
                Env.get().context.packageName,
                SERVER_CLASS.AIDL
            )
            val intent = Intent()
            intent.putExtra("ip", Env.get().runTime.ip)
            intent.putExtra("runMode", Env.get().runTime.runMode)
            intent.setComponent(componentName)
            Env.get().context.startService(intent)
            Env.get().context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    fun Unbind() {
        if (isConnected && isBind) {
            XLog.i("销毁运行进程")
            Env.get().context.unbindService(serviceConnection)
            val componentName = ComponentName(
                Env.get().context.packageName,
                SERVER_CLASS.AIDL
            )
            val intent = Intent()
            intent.setComponent(componentName)
            Env.get().context.stopService(intent)
            isBind = false
            isConnected = false
        } else {
            XLog.i("暂无运行进程")
        }
    }

}