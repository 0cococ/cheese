package coco.cheese.pack
import coco.cheese.core.Env

class App :coco.cheese.core.App() {

    override fun onCreate() {
        super.onCreate()
        Env.init(true,this)

    }


}