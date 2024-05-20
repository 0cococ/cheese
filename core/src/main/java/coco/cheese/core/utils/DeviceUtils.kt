package coco.cheese.core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import java.lang.ref.WeakReference

class DeviceUtils(private val env: Env) {
    fun getIMEI(): String {
        return DeviceIdentifier.getIMEI(env.context)
    }
    fun supportedOAID(): Boolean {
        return DeviceID.supportedOAID(env.context)
    }
    fun getOAID(): String {
        return DeviceID.getUniqueID(env.context)
    }
    internal class LocationUtil(private val context: Context) {
        private val locationManager: LocationManager? = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        private var locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: android.location.Location) {
                lastLocation = location
            }

            override fun onProviderDisabled(provider: String) {}
            override fun onProviderEnabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        }
        var lastLocation: android.location.Location? = null

        fun checkPermission(): Boolean {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(requestCode: Int) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
        }

        val isGpsEnabled: Boolean
            get() = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false

        fun start() {
            if (checkPermission() && isGpsEnabled) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0f,
                    locationListener
                )
                lastLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        }

        fun stop() {
            locationManager?.removeUpdates(locationListener)
        }
    }

    fun getPosition(): String? {
        val locationUtil = LocationUtil(env.context)
        if (!locationUtil.checkPermission()) {
            locationUtil.requestPermission(100)
            return null
        } else {
            if (!locationUtil.isGpsEnabled) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                env.context.startActivity(intent)
                return null
            }
        }
        locationUtil.start()
        val location: android.location.Location? = locationUtil.lastLocation
        if (location != null) {
            val longitude = location.longitude
            val latitude = location.latitude
            return "$longitude,$latitude"
        }
        locationUtil.stop()
        return null
    }
//    internal class Location(val context: Context) {
//        private var locationManager: LocationManager? = null
//        private var locationListener: LocationListener? = null
//        var lastLocation: android.location.Location? = null
//        fun checkPermission(): Boolean {
//            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        }
//        fun requestPermission(requestCode: Int) {
//            ActivityCompat.requestPermissions(
//                (context as Activity?)!!,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                requestCode
//            )
//        }
//
//        val isGpsEnabled: Boolean
//            get() = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
//
//        fun start() {
//            if (checkPermission() && isGpsEnabled) {
//                locationManager!!.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 0, 0f,
//                    locationListener!!
//                )
//                lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            }
//        }
//
//        fun stop() {
//            locationManager!!.removeUpdates(locationListener!!)
//        }
//
//        init {
//            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            locationListener = object : LocationListener {
//                override fun onLocationChanged(location: android.location.Location) {
//                    lastLocation = location
//                }
//                override fun onProviderDisabled(provider: String) {}
//                override fun onProviderEnabled(provider: String) {}
//                @Deprecated("Deprecated in Java")
//                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//            }
//        }
//    }
//    fun getPosition(context: Context=env.context): String? {
//        val locationUtil = Location(context)
//        if (!locationUtil.checkPermission()) {
//            locationUtil.requestPermission(100)
//            return null
//        } else {
//            if (!locationUtil.isGpsEnabled) {
//                // 打开 GPS 设置界面
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                context.startActivity(intent)
//                return null
//            }
//        }
//        locationUtil.start()
//        val location: android.location.Location? = locationUtil.lastLocation
//        if (location != null) {
//            val longitude = location.longitude
//            val latitude = location.latitude
//            return "$longitude,$latitude"
//        }
//        locationUtil.stop()
//        return null
//    }

    fun getPublicIP(url:String): String {
        return HttpUtils.get().get(url)
    }


    fun getWifiIP(context: Context=env.context): String {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        @SuppressLint("MissingPermission") val wifiInfo =
            wifiManager.connectionInfo
        val ipAddress = wifiInfo.getIpAddress()
        return String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }
    fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android $release (API level $sdkVersion)"
    }

    /**
     * 获取状态栏高度
     * @param context 上下文环境
     * @return 状态栏高度
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(context: Context = env.context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getNavigationBarHeight(context: Context=env.context): Int {
        if (hasNavigationBar(context)) {
            val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            // 如果找到了对应的资源标识符，则返回导航栏高度的像素值
            if (resourceId > 0) {
                return context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return 0
    }


    private fun hasNavigationBar(context: Context): Boolean {
        val display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val realSize = Point()
        val screenSize = Point()
        display.getRealSize(realSize)
        display.getSize(screenSize)
        return realSize.y != screenSize.y
    }
    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels+ getNavigationBarHeight()
    }

    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }


    fun getScreenDpi(): Int {
        return Resources.getSystem().displayMetrics.densityDpi
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun getClipboard(): String? {
        val clipboard = env.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // 检查剪贴板是否为空
        if (!clipboard.hasPrimaryClip()) {
            return null
        }
        val clipData = clipboard.primaryClip

        // 检查剪贴板数据项是否为空
        if (clipData == null || clipData.itemCount == 0) {
            return null
        }
        val item = clipData.getItemAt(0)
        val text = item.text ?: return null

        // 检查剪贴板文本是否为空
        return text.toString()
    }

    fun setClipboard( content: String?): Boolean {
        return try {
            val clipboard = env.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // 创建ClipData对象，并指定MIME类型为普通文本
            val clipData = ClipData.newPlainText("text", content)

            // 将ClipData对象放入剪贴板
            clipboard.setPrimaryClip(clipData)
            true // 写入剪贴板成功
        } catch (e: Exception) {
            e.printStackTrace()
            false // 写入剪贴板失败
        }
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<DeviceUtils>? = null
        private var instance: DeviceUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean) : DeviceUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = DeviceUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): DeviceUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(DeviceUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }


}
