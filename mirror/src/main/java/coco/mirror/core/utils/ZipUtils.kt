package coco.mirror.core.utils

import coco.mirror.core.Env
import coco.mirror.core.interfaces.IBase
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference

class ZipUtils(private val env: Env) {

    @Throws(IOException::class)
    fun extractLibsFromApk(apkFilePath: String, destinationFolderPath: String) {
        val apkFile = java.util.zip.ZipFile(apkFilePath)
        val entries = apkFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            if (entry.isDirectory || !entry.name.startsWith("lib/")) {
                continue
            }
            val entryName = entry.name
            val libName = entryName.substring(entryName.indexOf('/') + 1)
            val libFile = File(destinationFolderPath + File.separator + libName)
            if (libFile.getParentFile() != null && !libFile.getParentFile()!!.exists()) {
                libFile.getParentFile()?.mkdirs()
            }
            val inputStream = apkFile.getInputStream(entry)
            val outputStream = FileOutputStream(libFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            inputStream.close()
            outputStream.close()
        }
        apkFile.close()
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<ZipUtils>? = null
        private var instance: ZipUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ZipUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ZipUtils(env)
                }
            }
            return this.instance!!
        }
        override fun getWeak(env: Env, examine: Boolean): ZipUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ZipUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }
    }
}