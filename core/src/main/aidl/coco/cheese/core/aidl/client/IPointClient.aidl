package coco.cheese.core.aidl.client;



interface IPointClient {
   boolean swipeToPoint(int sx, int sy, int ex, int ey, int dur);
   boolean clickPoint(int sx, int sy);
   boolean longClickPoint(int sx, int sy);
   boolean touchDown(int x, int y);
   boolean touchMove(int x, int y);
   boolean touchUp();
}