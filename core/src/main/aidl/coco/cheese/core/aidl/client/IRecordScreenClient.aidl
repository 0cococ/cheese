package coco.cheese.core.aidl.client;

interface IRecordScreenClient {

   boolean requestPermission(int timeout);
   boolean checkPermission();
   byte[] captureScreen (int timeout ,int x, int y, int ex, int ey);

}