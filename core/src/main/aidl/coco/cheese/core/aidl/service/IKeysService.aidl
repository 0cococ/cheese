package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.client.IKeysClient;
interface IKeysService {
  void register(in IKeysClient callback);
}