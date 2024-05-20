package coco.cheese.core.hook

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.utils.ui.XmlUtils
import java.lang.ref.WeakReference

class ResourceHook(private val env: Env) {

    private var resources: Resources? = null


    @SuppressLint("PrivateApi")
    @Synchronized
     fun loadRes(context: Context=env.context, apkFilePath: String) {
        try {
            // 先创建AssetManager
            val assetManager = AssetManager::class.java.newInstance()
            AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).invoke(
                assetManager, apkFilePath
            )
            //在创建Resource
            resources = Resources(
                assetManager,
                context.resources.displayMetrics,
                context.resources.configuration
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDrawable(name: String, packageName: String): Drawable? {
        var imgId = getId(name, "mipmap", packageName)
        if (imgId == 0) {
            imgId = getId(name, "drawable", packageName)
        }
        return ResourcesCompat.getDrawable(resources!!, imgId, null)
    }


    @SuppressLint("DiscouragedApi")
    fun getId(name: String, type: String, packageName: String): Int {
        return resources!!.getIdentifier(name, type, packageName)
    }

    fun getLayout(id: Int): XmlResourceParser {
        return resources!!.getLayout(id)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ResourceHook>? = null
        private var instance: ResourceHook? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : ResourceHook {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ResourceHook(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ResourceHook {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ResourceHook(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}