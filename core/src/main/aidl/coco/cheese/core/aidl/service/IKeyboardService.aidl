package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.client.IKeyboardClient;
interface IKeyboardService {
  void register(in IKeyboardClient callback);
}