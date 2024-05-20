package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.client.IEnvClient;
interface IEnvService {
  void register(in IEnvClient callback);
  void start();
  void exit();
}