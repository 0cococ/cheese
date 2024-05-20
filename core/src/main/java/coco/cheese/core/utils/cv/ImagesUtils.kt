package coco.cheese.core.utils.cv

import android.graphics.Bitmap
import android.util.Log
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import org.opencv.android.Utils
import org.opencv.calib3d.Calib3d
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfDMatch
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.ORB
import org.opencv.features2d.SIFT
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.lang.ref.WeakReference

class ImagesUtils(private val env: Env) {
    /**
     * Sift-同分
     *
     * @param inputImage   输入图像路径
     * @param targetImage  目标图像路径
     * @param threshold    匹配阈值
     * @return 返回目标图像在输入图像中的位置，未找到返回null
     */
    fun findImgBySift(inputImage: Bitmap, targetImage: Bitmap, threshold: Double): Point? {
        // 读取输入图像和目标图像

        val imgScene = Mat()
        // 读取图像
        val imgObject = Mat()
        Utils.bitmapToMat(inputImage, imgScene, false)
        Utils.bitmapToMat(targetImage, imgObject, false)
//    val imgScene = Imgcodecs.imread(inputImage, Imgcodecs.IMREAD_GRAYSCALE)
//    val imgObject = Imgcodecs.imread(targetImage, Imgcodecs.IMREAD_GRAYSCALE)

        // 检查图像是否为空
        if (imgScene.empty() || imgObject.empty()) {
            Log.e("ImageDebug", "Input image or target image is empty")
            return null
        }

        Log.d("ImageDebug", "imgScene size: ${imgScene.size()}")
        Log.d("ImageDebug", "imgObject size: ${imgObject.size()}")

        // 创建 SIFT 检测器
        val sift = SIFT.create()

        // 检测和计算描述符
        val keypointsObject = MatOfKeyPoint()
        val keypointsScene = MatOfKeyPoint()
        val descriptorsObject = Mat()
        val descriptorsScene = Mat()

        sift.detectAndCompute(imgObject, Mat(), keypointsObject, descriptorsObject)
        sift.detectAndCompute(imgScene, Mat(), keypointsScene, descriptorsScene)

        Log.d("ImageDebug", "keypointsObject size: ${keypointsObject.size()}")
        Log.d("ImageDebug", "keypointsScene size: ${keypointsScene.size()}")

        // 创建 BFMatcher（暴力匹配器）
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
        val matches = MatOfDMatch()
        matcher.match(descriptorsObject, descriptorsScene, matches)

        Log.d("ImageDebug", "Number of matches: ${matches.rows()}")

        // 筛选匹配点
        val matchesList = matches.toList()
        val goodMatchesList = matchesList.filter { it.distance < threshold }

        Log.d("ImageDebug", "Number of good matches: ${goodMatchesList.size}")

        // 如果找到足够的匹配点，计算图像的位置
        if (goodMatchesList.size > 10) {
            val keypointsObjectList = keypointsObject.toList()
            val keypointsSceneList = keypointsScene.toList()

            val objList = mutableListOf<Point>()
            val sceneList = mutableListOf<Point>()
            goodMatchesList.forEach { match ->
                objList.add(keypointsObjectList[match.queryIdx].pt)
                sceneList.add(keypointsSceneList[match.trainIdx].pt)
            }

            val obj = MatOfPoint2f()
            obj.fromList(objList)

            val scene = MatOfPoint2f()
            scene.fromList(sceneList)

            val H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 3.0)

            val objCorners = Mat(4, 1, CvType.CV_32FC2)
            val sceneCorners = Mat(4, 1, CvType.CV_32FC2)

            // 定义目标图像的四个角点
            objCorners.put(0, 0, 0.0, 0.0)
            objCorners.put(1, 0, imgObject.cols().toDouble(), 0.0)
            objCorners.put(2, 0, imgObject.cols().toDouble(), imgObject.rows().toDouble())
            objCorners.put(3, 0, 0.0, imgObject.rows().toDouble())

            // 计算目标图像的四个角点在输入图像中的位置
            Core.perspectiveTransform(objCorners, sceneCorners, H)

            val sceneCornersList = mutableListOf<Point>()
            for (i in 0 until sceneCorners.rows()) {
                val point = sceneCorners.get(i, 0)
                sceneCornersList.add(Point(point[0], point[1]))
            }

            // 计算矩形的中心点
            val tl = sceneCornersList[0]
            val br = sceneCornersList[2]
            val centerX = (tl.x + br.x) / 2
            val centerY = (tl.y + br.y) / 2

            // 释放资源
            imgScene.release()
            imgObject.release()
            keypointsObject.release()
            keypointsScene.release()
            descriptorsObject.release()
            descriptorsScene.release()
            matches.release()

            // 返回矩形的中心点坐标
            return Point(centerX, centerY)
        } else {
            // 释放资源
            imgScene.release()
            imgObject.release()
            keypointsObject.release()
            keypointsScene.release()
            descriptorsObject.release()
            descriptorsScene.release()
            matches.release()

            return null
        }

    }


    fun findImgByOBR(inputImage: Bitmap, targetImage: Bitmap, threshold: Double): Point? {


        val imgScene = Mat()
        // 读取图像
        val imgObject = Mat()
        Utils.bitmapToMat(inputImage, imgScene, false)
        Utils.bitmapToMat(targetImage, imgObject, false)
        // 检查图像是否为空
        if (imgScene.empty() || imgObject.empty()) {
            println("Input image or target image is empty")
            return null
        }

        // 创建 ORB 检测器
        val orb = ORB.create()

        // 检测关键点和计算描述符
        val keypointsObject = MatOfKeyPoint()
        val keypointsScene = MatOfKeyPoint()
        val descriptorsObject = Mat()
        val descriptorsScene = Mat()

        orb.detectAndCompute(imgObject, Mat(), keypointsObject, descriptorsObject)
        orb.detectAndCompute(imgScene, Mat(), keypointsScene, descriptorsScene)

        // 使用 BFMatcher 匹配器匹配描述符
        val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING)
        val matches = MatOfDMatch()
        matcher.match(descriptorsObject, descriptorsScene, matches)

        // 筛选匹配点
        val matchesList = matches.toList()
        val goodMatchesList = matchesList.filter { it.distance < threshold }

        // 如果找到足够的匹配点，计算图像的位置
        if (goodMatchesList.size > 10) {
            val keypointsObjectList = keypointsObject.toList()
            val keypointsSceneList = keypointsScene.toList()

            val objList = mutableListOf<Point>()
            val sceneList = mutableListOf<Point>()
            goodMatchesList.forEach { match ->
                objList.add(keypointsObjectList[match.queryIdx].pt)
                sceneList.add(keypointsSceneList[match.trainIdx].pt)
            }

            val obj = MatOfPoint2f()
            obj.fromList(objList)

            val scene = MatOfPoint2f()
            scene.fromList(sceneList)

            val H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 3.0)

            val objCorners = Mat(4, 1, CvType.CV_32FC2)
            val sceneCorners = Mat(4, 1, CvType.CV_32FC2)

            // 定义目标图像的四个角点
            objCorners.put(0, 0, 0.0, 0.0)
            objCorners.put(1, 0, imgObject.cols().toDouble(), 0.0)
            objCorners.put(2, 0, imgObject.cols().toDouble(), imgObject.rows().toDouble())
            objCorners.put(3, 0, 0.0, imgObject.rows().toDouble())

            // 计算目标图像的四个角点在输入图像中的位置
            Core.perspectiveTransform(objCorners, sceneCorners, H)

            val sceneCornersList = mutableListOf<Point>()
            for (i in 0 until sceneCorners.rows()) {
                val point = sceneCorners.get(i, 0)
                sceneCornersList.add(Point(point[0], point[1]))
            }

            // 计算矩形的中心点
            val tl = sceneCornersList[0]
            val br = sceneCornersList[2]
            val centerX = (tl.x + br.x) / 2
            val centerY = (tl.y + br.y) / 2

            // 返回矩形的中心点坐标
            return Point(centerX, centerY)
        } else {
            return null
        }
    }

    /**
     * 对图像进行二值化处理。
     *
     * @param inputImage 图像的文件路径
     * @param threshold 二值化阈值
     * @return 二值化后的图像的 Mat 对象，如果发生错误，则返回 null
     */
    fun binarization(inputImage: String, threshold: Double): Bitmap? {
        try {
            // 读取图像
            val mat = Imgcodecs.imread(inputImage, Imgcodecs.IMREAD_GRAYSCALE)

            // 检查图像是否成功读取
            if (mat.empty()) {
                Log.e("ImageUtils", "Error: Failed to read image")
                return null
            }

            // 创建用于存储二值化后图像的 Mat 对象
            val binaryMat = Mat()

            // 进行二值化处理
            Imgproc.threshold(mat, binaryMat, threshold, 255.0, Imgproc.THRESH_BINARY)
            val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(binaryMat, bitmap)
            // 返回二值化后的图像
            return bitmap
        } catch (e: Exception) {
            // 发生异常，记录错误并返回空
            Log.e("ImageUtils", "Error: ${e.message}")
            return null
        }
    }
    companion object : IBase {
        private var instanceWeak: WeakReference<ImagesUtils>? = null
        private var instance: ImagesUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): ImagesUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = ImagesUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): ImagesUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(ImagesUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }


    }
}