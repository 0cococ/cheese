package coco.cheese.core.aidl.client

import coco.cheese.core.utils.KeysUtils

class KeysClient:IKeysClient.Stub() {
    override fun home(): Boolean {
      return  KeysUtils.get().home()
    }

    override fun back(): Boolean {
        return  KeysUtils.get().back()
    }

    override fun quickSettings(): Boolean {
        return  KeysUtils.get().quickSettings()
    }

    override fun powerDialog(): Boolean {
        return  KeysUtils.get().powerDialog()
    }

    override fun pullNotificationBar(): Boolean {
        return  KeysUtils.get().pullNotificationBar()
    }

    override fun recents(): Boolean {
        return  KeysUtils.get().recents()
    }

    override fun lockScreen(): Boolean {
        return  KeysUtils.get().lockScreen()
    }

    override fun screenShot(): Boolean {
        return  KeysUtils.get().screenShot()
    }

    override fun splitScreen(): Boolean {
        return  KeysUtils.get().splitScreen()
    }
}