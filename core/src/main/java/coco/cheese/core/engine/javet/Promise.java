package coco.cheese.core.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValuePromise;
import com.elvishew.xlog.XLog;

import java.util.concurrent.TimeUnit;

public class Promise {
    public Promise(NodeRuntime nodeRuntime, Task task, Object value){
        if (task == null) {
            return;
        }
        try (V8ValuePromise promise = task.promise) {
            if (value == null) {
                promise.reject(nodeRuntime.createV8ValueUndefined());
                task=null;
            } else {

                promise.resolve(value);
                task=null;
            }
        } catch (Throwable t) {
            XLog.tag("Logger").e(t);
            t.printStackTrace(System.err);
        }
        if (nodeRuntime.getJSRuntimeType().isNode()) {
            nodeRuntime.await();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);

        }
        if (task!=null) {
            try (V8ValuePromise promise = task.promise) {
                promise.reject(nodeRuntime.createV8ValueUndefined());
            } catch (JavetException e) {
                e.printStackTrace(System.err);
            }
        }

    }
    public Promise(NodeRuntime nodeRuntime, Task task){
        if (task == null) {
            return;
        }
        try (V8ValuePromise promise = task.promise) {
                promise.reject(nodeRuntime.createV8ValueUndefined());
                task=null;
        } catch (Throwable t) {
            XLog.tag("Logger").e(t);
            t.printStackTrace(System.err);
        }
        if (nodeRuntime.getJSRuntimeType().isNode()) {
            nodeRuntime.await();
        }
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);

        }
        if (task!=null) {
            try (V8ValuePromise promise = task.promise) {
                promise.reject(nodeRuntime.createV8ValueUndefined());
            } catch (JavetException e) {
                e.printStackTrace(System.err);
            }
        }

    }

    public static class Task {
        private final V8ValuePromise promise;
        private final long timeout;
        private final Object value;
        public Task(V8ValuePromise promise,Object value, long timeout) {
            this.promise = promise;
            this.timeout = timeout;
            this.value=value;
        }
    }




}
