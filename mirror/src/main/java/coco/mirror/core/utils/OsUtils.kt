package coco.mirror.core.utils

import android.content.Context
import coco.mirror.core.Env
import java.io.File

val ROOT_DIRECTORY: File = Env.get().context.cacheDir.parentFile!!
val DATA_DIRECTORY: File = File(ROOT_DIRECTORY, "data")
val APP_DIRECTORY: File = File(DATA_DIRECTORY, "app")
 var APP_THAT_DIRECTORY: File =File("")
    set(value) {
        field = File(APP_DIRECTORY, "$value")
        APP_THAT_LIB_DIRECTORY = File(field, "lib")
        APP_THAT_THIS = File(APP_THAT_DIRECTORY, "base.apk")
        FilesUtils.get().create(APP_THAT_LIB_DIRECTORY.path)
    }

lateinit var APP_THAT_LIB_DIRECTORY: File
    private set

lateinit var APP_THAT_THIS: File
    private set
