package coco.cheese.core.aidl.client;
interface IEnvClient {
  void setTextList(String str);
  void startActivity(String activityId,String viewID, String  callbackID);
}