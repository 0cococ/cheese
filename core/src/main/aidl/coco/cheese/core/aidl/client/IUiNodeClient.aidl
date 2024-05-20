
package coco.cheese.core.aidl.client;
import coco.cheese.core.aidl.type.NodeType;

interface IUiNodeClient {
  boolean clearNodeCache();
  List<String> findAllWith();
  boolean click(in String node);
  boolean globalLongClick(in String node);
  boolean globalClick(in String node);
  boolean longClick(in String node);
  boolean doubleClick(in String node);
  boolean tryLongClick(in String node);
  boolean tryClick(in String node);
  List<String> get( String node, String name,in String[] args);
}