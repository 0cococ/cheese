package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.client.IRecordScreenClient;
interface IRecordScreenService {
  void register(in IRecordScreenClient callback);
}