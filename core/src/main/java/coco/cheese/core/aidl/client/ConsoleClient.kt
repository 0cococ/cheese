package coco.cheese.core.aidl.client

import coco.cheese.core.runOnUi
import coco.cheese.core.utils.ConsoleUtils

class ConsoleClient : IConsoleClient.Stub() {
    override fun show() {
        runOnUi {
            ConsoleUtils.get().show()
        }

    }

    override fun log(log: String) {
        runOnUi {
            ConsoleUtils.get().log(log)
        }

    }

    override fun clear() {
        runOnUi {
            ConsoleUtils.get().clear()
        }

    }

    override fun hide() {
        runOnUi {
            ConsoleUtils.get().hide()
        }

    }

    override fun setTouch(enabled: Boolean) {
        runOnUi {
            ConsoleUtils.get().setTouch(enabled)
        }
    }


}