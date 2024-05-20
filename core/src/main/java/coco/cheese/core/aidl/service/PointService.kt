package coco.cheese.core.aidl.service

import coco.cheese.core.AIDL
import coco.cheese.core.Env

import coco.cheese.core.aidl.client.IPointClient

class PointService:IPointService.Stub() {
    override fun register(callback: IPointClient) {
        Env.get().javaObjects[AIDL.CLIENT]!![IPointClient::class.simpleName.toString()] =callback
    }
}