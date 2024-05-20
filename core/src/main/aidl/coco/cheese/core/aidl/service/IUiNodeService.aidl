package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.type.NodeType;
import coco.cheese.core.aidl.client.IUiNodeClient;
interface IUiNodeService {
  void register(in IUiNodeClient callback);
  boolean callbackMethod(in NodeType nodeType);


}