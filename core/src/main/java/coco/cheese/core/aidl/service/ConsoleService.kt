package coco.cheese.core.aidl.service
import coco.cheese.core.AIDL
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IConsoleClient

class ConsoleService:IConsoleService.Stub() {
    override fun register(callback: IConsoleClient) {
        Env.get().javaObjects[AIDL.CLIENT]!![IConsoleClient::class.simpleName.toString()] =callback
    }

}