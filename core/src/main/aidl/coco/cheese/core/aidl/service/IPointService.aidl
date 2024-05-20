package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.client.IPointClient;
interface IPointService {
  void register(in IPointClient callback);
}