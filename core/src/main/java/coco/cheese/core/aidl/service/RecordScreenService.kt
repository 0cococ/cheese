package coco.cheese.core.aidl.service

import coco.cheese.core.AIDL
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IPointClient
import coco.cheese.core.aidl.client.IRecordScreenClient

class RecordScreenService:IRecordScreenService.Stub() {
    override fun register(callback: IRecordScreenClient) {
        Env.get().javaObjects[AIDL.CLIENT]!![IRecordScreenClient::class.simpleName.toString()] =callback
    }
}