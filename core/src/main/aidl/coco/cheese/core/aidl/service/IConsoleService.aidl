
package coco.cheese.core.aidl.service;
import coco.cheese.core.aidl.client.IConsoleClient;
interface IConsoleService {
  void register(in IConsoleClient callback);
}