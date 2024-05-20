package coco.cheese.core.service;

import static org.koin.java.KoinJavaComponent.get;

import android.inputmethodservice.InputMethodService;

import com.elvishew.xlog.XLog;

import coco.cheese.core.Env;


public class MyInputMethodService extends InputMethodService {
    Env env = get(Env.class);
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.i("初始化输入法");
        env.setInputMethodService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
