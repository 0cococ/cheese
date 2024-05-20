package coco.cheese.core.aidl.service

import coco.cheese.core.AIDL
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IKeyboardClient
import coco.cheese.core.aidl.client.IKeysClient

class KeyboardService:IKeyboardService.Stub() {
    override fun register(callback: IKeyboardClient) {
        Env.get().javaObjects[AIDL.CLIENT]!![IKeyboardClient::class.simpleName.toString()] =callback
    }
}