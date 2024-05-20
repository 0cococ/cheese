package coco.cheese.core.aidl.server

import coco.cheese.core.AIDL
import coco.cheese.core.BASE
import coco.cheese.core.Env


import coco.cheese.core.aidl.client.IUiNodeClient

import coco.cheese.core.aidl.service.IUiNodeService
import coco.cheese.core.aidl.type.NodeType
import coco.cheese.core.callback.NodeCallBack

class UiNodeService: IUiNodeService.Stub() {
    override fun register(callback: IUiNodeClient) {
        Env.get().javaObjects[AIDL.CLIENT]!![IUiNodeClient::class.simpleName.toString()] =callback
    }
    override fun callbackMethod(nodeType: NodeType): Boolean {
        val nodeCallBack= Env.get().javaObjects[BASE]!!["findAllWith"] as NodeCallBack
        return nodeCallBack.getCallBack1()!!.callbackMethod(nodeType)
    }
}