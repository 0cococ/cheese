package coco.cheese.core.utils.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import coco.cheese.core.BASE
import coco.cheese.core.BIND
import coco.cheese.core.Env
import coco.cheese.core.aidl.client.IEnvClient
import coco.cheese.core.aidl.client.IUiNodeClient
import coco.cheese.core.callback.IActivity
import coco.cheese.core.extractPackageName
import coco.cheese.core.hook.ResourceHook
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.utils.AppUtils
import coco.cheese.core.utils.ClassUtils
import coco.cheese.core.utils.FilesUtils
import coco.cheese.core.utils.StringUtils
import coco.runui.ExportApi
import coco.runui.ID
import com.elvishew.xlog.XLog
import com.hjq.window.EasyWindow
import java.io.File
import java.io.FileInputStream
import java.lang.ref.WeakReference

data class View_(
    var view: View,
    var id: String,
)

class XmlUtils(private val env: Env) {

    var viewID = ""
    var packageName = ""
    fun parseApkXml(name: String): View {
        val context = env.context ?: throw IllegalStateException("Context is null")
        packageName = env.context.packageName
        viewID = "${this.javaClass.simpleName}@${StringUtils.get().getRandomString(6)}"
        val layoutResId = try {
            val layoutClass = Class.forName("${context.packageName}.R\$layout")
            val field = layoutClass.getField(name)
            field.getInt(null)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        }
        val inflatedView = LayoutInflater.from(context).inflate(layoutResId, null)
        val view = View_(inflatedView, viewID)
        Env.get().javaObjects[BASE]!![viewID] =
            view
        return view.view

    }

    fun parseApkXml(path: String, name: String): View {
        val context = env.context ?: throw IllegalStateException("Context is null")
        packageName = extractPackageName(path)!!
        viewID = "${this.javaClass.simpleName}@${StringUtils.get().getRandomString(6)}"
        ClassUtils.get().loadDex(path)
        ResourceHook.get().loadRes(apkFilePath = path)
        val inflater = LayoutInflater.from(context)
        val layoutId =
            ResourceHook.get().getLayout(ResourceHook.get().getId(name, "layout", packageName))
        val inflatedView = inflater.inflate(layoutId, null, true)
        val view = View_(inflatedView, viewID)
        Env.get().javaObjects[BASE]!![viewID] =
            view
        return view.view

    }
    fun parseXml(xml: String): View {
        viewID = "${this.javaClass.simpleName}@${StringUtils.get().getRandomString(6)}"
        val view= if (FilesUtils.get().isFile(xml)){
            View_( ExportApi().parseXml(FileInputStream(File(xml))), viewID)
        }else{
            View_( ExportApi().parseXml(xml), viewID)
        }
        Env.get().javaObjects[BASE]!![viewID] =
            view
        return view.view
    }


    fun getID(id: String): Int {
        return ID[id]!!
    }





    fun findR(id: String): Int {
        try {
            // 获取类对象
            val myClass = Class.forName("$packageName.R\$id")
            // 获取指定静态字段
            val field = myClass.getDeclaredField(id)

            // 设置字段可访问
            field.isAccessible = true

            // 获取静态字段的值
            val fieldValue = field.get(null)

            return fieldValue as? Int ?: 0
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return 0
    }

    fun inflateView(context: Context = env.context, viewId: Int): View {
        return LayoutInflater.from(context).inflate(viewId, null, true)
    }

    companion object : IBase {
        private var instanceWeak: WeakReference<XmlUtils>? = null
        private var instance: XmlUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): XmlUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = XmlUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): XmlUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(XmlUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }
}