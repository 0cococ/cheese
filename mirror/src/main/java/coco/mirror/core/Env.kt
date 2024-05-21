package coco.mirror.core

import android.content.Context
import android.content.res.AssetManager
import org.koin.java.KoinJavaComponent

import java.lang.ref.WeakReference

class Env {
    lateinit var context: Context
    var app: AppNest = AppNest()
    companion object {
        private var envRef: WeakReference<Env>? = null
        private var env: Env? = null
        private val lock = Any()
        @JvmOverloads
        fun get(examine: Boolean = true): Env {
            if (this.env == null || !examine) {
                synchronized(this.lock) {
                    this.env = KoinJavaComponent.get<Env>(Env::class.java)
                }
            }

            return this.env!!

        }

        @JvmOverloads
        fun getWeak(examine: Boolean = true): Env {
            if (this.envRef?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.envRef = WeakReference(KoinJavaComponent.get<Env>(Env::class.java))
                }
            }

            return this.envRef?.get()!!

        }
    }
    data class AppNest(
        var path: String = "",
        var `package`: String = "",
    )

}