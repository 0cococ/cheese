package coco.cheese

import android.app.Application
import coco.cheese.core.Env

class App :coco.cheese.core.App() {

    override fun onCreate() {
        super.onCreate()
        Env.init(false,this)

    }


}