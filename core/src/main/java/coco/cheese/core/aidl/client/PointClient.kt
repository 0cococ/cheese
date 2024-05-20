package coco.cheese.core.aidl.client

import coco.cheese.core.utils.PointUtils

class PointClient:IPointClient.Stub() {
    override fun swipeToPoint(sx: Int, sy: Int, ex: Int, ey: Int, dur: Int): Boolean {
       return PointUtils.get().swipeToPoint(sx,sy,ex,ey,dur)
    }

    override fun clickPoint(sx: Int, sy: Int): Boolean {
        return PointUtils.get().clickPoint(sx,sy)
    }

    override fun longClickPoint(sx: Int, sy: Int): Boolean {
        return PointUtils.get().longClickPoint(sx,sy)
    }

    override fun touchDown(x: Int, y: Int): Boolean {
        return PointUtils.get().touchDown(x,y)
    }

    override fun touchMove(x: Int, y: Int): Boolean {
        return PointUtils.get().touchMove(x,y)
    }

    override fun touchUp(): Boolean {
        return PointUtils.get().touchUp()
    }
}