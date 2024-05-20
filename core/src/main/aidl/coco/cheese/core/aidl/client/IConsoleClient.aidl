package coco.cheese.core.aidl.client;

interface IConsoleClient {
   void show();
   void log(String log);
   void clear();
   void hide();
   void setTouch(boolean enabled);

}