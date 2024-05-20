package coco.cheese.core.aidl.client

import coco.cheese.core.utils.KeyboardUtils

class KeyboardClient: IKeyboardClient.Stub() {


    override fun input(text: String) {
        KeyboardUtils.get().input(text)
    }

    override fun delete() {
        KeyboardUtils.get().delete()
    }

    override fun enter() {
        KeyboardUtils.get().enter()
    }

}