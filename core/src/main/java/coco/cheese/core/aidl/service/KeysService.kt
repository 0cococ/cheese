package coco.cheese.core.aidl.service

import coco.cheese.core.AIDL
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IKeysClient

class KeysService:IKeysService.Stub() {
    override fun register(callback: IKeysClient) {
        Env.get().javaObjects[AIDL.CLIENT]!![IKeysClient::class.simpleName.toString()] =callback
    }
}