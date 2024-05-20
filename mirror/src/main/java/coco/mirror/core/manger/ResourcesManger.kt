package coco.mirror.core.manger

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.res.ResourcesCompat
import coco.mirror.core.Env

import coco.mirror.core.interfaces.IBase
import coco.mirror.core.utils.PackageUtils
import java.lang.ref.WeakReference

class ResourcesManger(private val env: Env) {
    lateinit var resources: Resources
    private lateinit var pkg: String
    @SuppressLint("PrivateApi")
   fun loadRes(apkFilePath: String) {
        pkg =PackageUtils.get(). extractPackageName(apkFilePath) ?: return
        try {
            val assetManager = AssetManager::class.java.newInstance()
            AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java).invoke(
                assetManager, apkFilePath
            )
            resources = Resources(
                assetManager,
                env.context.resources.displayMetrics,
                env.context.resources.configuration
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRes(name: String): Drawable? {
        val id = getID(name, "mipmap", pkg).takeIf { it != 0 } ?: getID(name, "drawable", pkg)
        return ResourcesCompat.getDrawable(resources, id, null)
    }

    @SuppressLint("DiscouragedApi")
    fun getID(name: String, type: String, packageName: String): Int {
        return resources.getIdentifier(name, type, packageName)
    }

    fun getLayoutView(name: String): View? {
        val id=getID(name,"layout",pkg)
        val inflater = LayoutInflater.from(env.context)
        return inflater.inflate(resources.getLayout(id), null, true)
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<ResourcesManger>? = null
        private var instance: ResourcesManger? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ResourcesManger {
            if (instance == null || !examine) {
                synchronized(lock) {
                    instance = ResourcesManger(env)
                }
            }
            return instance!!
        }
        override fun getWeak(env: Env, examine: Boolean): ResourcesManger {
            if (instanceWeak?.get() == null || !examine) {
                synchronized(lock) {
                    instanceWeak = WeakReference(ResourcesManger(env))
                }
            }
            return instanceWeak?.get()!!
        }
    }

}