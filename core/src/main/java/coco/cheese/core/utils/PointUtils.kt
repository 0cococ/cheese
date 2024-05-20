package coco.cheese.core.utils

import android.graphics.Path
import android.util.Pair
import cn.vove7.andro_accessibility_api.requireBaseAccessibility
import cn.vove7.auto.core.api.click
import cn.vove7.auto.core.api.gesture
import cn.vove7.auto.core.api.longClick
import cn.vove7.auto.core.api.playGestures
import cn.vove7.auto.core.api.swipe
import cn.vove7.auto.core.utils.AutoGestureDescription
import cn.vove7.auto.core.utils.ScreenAdapter
import coco.cheese.core.Action
import coco.cheese.core.Action.Companion.runAction
import coco.cheese.core.Env
import coco.cheese.core.interfaces.IBase
import coco.cheese.core.t

import java.lang.ref.WeakReference
import java.util.LinkedList


class PointUtils(private val env: Env) {

    private val p = LinkedList<Pair<Int, Int>>()

    class _swipeToPoint : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 5) { "传入参数数量错误" }
            val (a, b, c, d, e) = parameters
            return try {
                val startX = a.toString().toFloat().toInt()
                val startY = b.toString().toFloat().toInt()
                val endX = c.toString().toFloat().toInt()
                val endY = d.toString().toFloat().toInt()
                val duration = e.toString().toFloat().toInt()
                swipe(startX, startY, endX, endY, duration)
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }
        }
    }

    fun swipeToPoint(vararg points: Any): Boolean {
        return runAction(_swipeToPoint(), *points) as Boolean
    }

    class _clickPoint : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 2) { "传入参数数量错误" }
            val (a, b) = parameters
            return try {
                val x = a.toString().toFloat().toInt()
                val y = b.toString().toFloat().toInt()
                click(x, y)
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }

        }
    }

    fun clickPoint(vararg points: Any): Boolean {
        return runAction(_clickPoint(), *points) as Boolean
    }

    class _longClickPoint : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 2) { "传入参数数量错误" }
            val (a, b) = parameters
            return try {
                val x = a.toString().toFloat().toInt()
                val y = b.toString().toFloat().toInt()
                longClick(x, y)
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }
        }
    }

    fun longClickPoint(vararg points: Any): Boolean {
        return runAction(_longClickPoint(), *points) as Boolean
    }

    class _gesture : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 2) { "传入参数数量错误" }
            val (a, b) = parameters
            return try {
                val x = a as Long
                val y = b as Array<Pair<Int, Int>>
                gesture(x, y)
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }
        }
    }

    fun gesture(vararg points: Any): Boolean {
        return runAction(_gesture(), *points) as Boolean
    }


    private fun pointsToPath(points: Array<Pair<Int, Int>>): Path {
        val path = Path()
        if (points.isEmpty()) return path
        path.moveTo(ScreenAdapter.scaleX(points[0].first), ScreenAdapter.scaleY(points[0].second))

        for (i in 1 until points.size) {
            path.lineTo(
                ScreenAdapter.scaleX(points[i].first),
                ScreenAdapter.scaleY(points[i].second)
            )
        }
        return path
    }

    class _touchDown : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 2) { "传入参数数量错误" }
            val (a, b) = parameters
            return try {
                val x = (a as Int).toLong()
                val y = b as Array<Pair<Int, Int>>
                val path = get().pointsToPath(y)
                playGestures(listOf(AutoGestureDescription.StrokeDescription(path, 0, x, true)))
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }
        }
    }

    fun touchDown(x: Int, y: Int): Boolean {
        val backup: Pair<Int, Int> = x t y
        if (!p.isEmpty()) {
            return false
        }
        p.add(backup)
        return runAction(
            _touchDown(),
            1000,
            ConvertersUtils.get().pairArray(backup.first, backup.second)
        ) as Boolean
    }

    class _touchMove : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 2) { "传入参数数量错误" }
            val (a, b) = parameters
            return try {
                val x = (a as Int).toLong()
                val y = b as Array<Pair<Int, Int>>
                val path = get().pointsToPath(y)
                playGestures(listOf(AutoGestureDescription.StrokeDescription(path, 0, x, true)))
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }
        }
    }

    fun touchMove(x: Int, y: Int): Boolean {
        var backup: Pair<Int, Int> = x t y
        if (!p.isEmpty()) {
            backup = p[0]
            p.clear()
        }
        p.add(x t y)
        return runAction(
            _touchMove(),
            1000,
            ConvertersUtils.get().pairArray(backup.first, backup.second, x, y)
        ) as Boolean
    }

    class _touchUp : Action() {
        override suspend fun run(vararg parameters: Any): Any {
            requireBaseAccessibility(true)
            require(parameters.size >= 2) { "传入参数数量错误" }
            val (a, b) = parameters
            return try {
                val x = (a as Int).toLong()
                val y = b as Array<Pair<Int, Int>>
                val path = get().pointsToPath(y)
                playGestures(listOf(AutoGestureDescription.StrokeDescription(path, 0, x, false)))
            } catch (e: NumberFormatException) {
                println("类型错误")
                false
            }
        }
    }

    fun touchUp(): Boolean {
        if (p.isEmpty()) {
            return false
        }
        val x = p[0].first
        val y = p[0].second
        p.clear()
        return runAction(
            _touchUp(),
            1000,
            ConvertersUtils.get().pairArray(x, y)
        ) as Boolean
    }

    fun touchDown0(x: Int,y: Int){
        RootUtils.get().sendEvent(3,0,x)
        RootUtils.get().sendEvent(3,1,y)
        RootUtils.get().sendEvent(1,330,1)
        RootUtils.get().sendEvent(0,0,0)
        RootUtils.get().sendEvent(1,330,0)
        RootUtils.get().sendEvent(0,0,0)
    }


    companion object : IBase {
        private var instanceWeak: WeakReference<PointUtils>? = null
        private var instance: PointUtils? = null
        private val lock = Any()
        override fun get(env: Env, examine: Boolean): PointUtils {
            if (this.instance == null || !examine) {
                synchronized(this.lock) {
                    this.instance = PointUtils(env)
                }
            }
            return this.instance!!
        }

        override fun getWeak(env: Env, examine: Boolean): PointUtils {
            if (this.instanceWeak?.get() == null || !examine) {
                synchronized(this.lock) {
                    this.instanceWeak = WeakReference(PointUtils(env))
                }
            }
            return this.instanceWeak?.get()!!
        }

    }

}