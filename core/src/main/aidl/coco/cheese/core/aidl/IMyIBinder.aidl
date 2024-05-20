package coco.cheese.core.aidl;
import android.os.IBinder;
interface IMyIBinder {
  IBinder getConsoleService();
  IBinder getKeyboardService();
  IBinder getPointService();
  IBinder getUiNodeService();
  IBinder getEnvService();
  IBinder getKeysService();
  IBinder getRecordScreenService();
}