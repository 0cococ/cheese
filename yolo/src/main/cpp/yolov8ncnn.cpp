// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>

#include <android/bitmap.h>
#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "yolo.h"

#include "ndkcamera.h"

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON
int globalIntVariable = 0;
jobjectArray globalStringArray = nullptr;

void convertJObjectArrayToCharArray(JNIEnv* env, jobjectArray stringArray, const char* class_names[]) {
    jsize length = env->GetArrayLength(stringArray);
    jstring javaString;
    const char* utfString;
    for (int i = 0; i < length; i++) {
        javaString = (jstring)env->GetObjectArrayElement(stringArray, i);
        utfString = env->GetStringUTFChars(javaString, 0);
        class_names[i] = utfString;
//        env->ReleaseStringUTFChars(javaString, utfString); // 释放内存
    }



    env->ReleaseStringUTFChars(javaString, utfString);


}

static int draw_unsupported(cv::Mat& rgb)
{
    const char text[] = "unsupported";

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 1.0, 1, &baseLine);

    int y = (rgb.rows - label_size.height) / 2;
    int x = (rgb.cols - label_size.width) / 2;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                  cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 1.0, cv::Scalar(0, 0, 0));

    return 0;
}

static int draw_fps(cv::Mat& rgb)
{
    // resolve moving average
    float avg_fps = 0.f;
    {
        static double t0 = 0.f;
        static float fps_history[10] = {0.f};

        double t1 = ncnn::get_current_time();
        if (t0 == 0.f)
        {
            t0 = t1;
            return 0;
        }

        float fps = 1000.f / (t1 - t0);
        t0 = t1;

        for (int i = 9; i >= 1; i--)
        {
            fps_history[i] = fps_history[i - 1];
        }
        fps_history[0] = fps;

        if (fps_history[9] == 0.f)
        {
            return 0;
        }

        for (int i = 0; i < 10; i++)
        {
            avg_fps += fps_history[i];
        }
        avg_fps /= 10.f;
    }

    char text[32];
    sprintf(text, "FPS=%.2f", avg_fps);

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 0.5, 1, &baseLine);

    int y = 0;
    int x = rgb.cols - label_size.width;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                  cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(0, 0, 0));

    return 0;
}

static Yolo* g_yolo = 0;
static ncnn::Mutex lock;

class MyNdkCamera : public NdkCameraWindow
{
public:
    virtual void on_image_render(cv::Mat& rgb) const;
};

void MyNdkCamera::on_image_render(cv::Mat& rgb) const
{

    {
        ncnn::MutexLockGuard g(lock);

        if (g_yolo)
        {
            std::vector<Object> objects;
            g_yolo->detect(rgb, objects);

            g_yolo->draw(rgb, objects);
        }
        else
        {
            draw_unsupported(rgb);
        }
    }

    draw_fps(rgb);
}

static MyNdkCamera* g_camera = 0;

extern "C" {
JNIEXPORT jboolean JNICALL isClass(JNIEnv* env, const char* className) {
    jclass loadedClass = env->FindClass(className);

    if (loadedClass != nullptr) {
        printf("Class %s is true.\n", className);
        return JNI_TRUE; // Class is loaded
    }
    printf("Class %s is false.\n", className);
    return JNI_FALSE; // Class is not loaded
}
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");

    g_camera = new MyNdkCamera;

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");

    {
        ncnn::MutexLockGuard g(lock);

        delete g_yolo;
        g_yolo = 0;
    }

    delete g_camera;
    g_camera = 0;
}

static jclass objCls = NULL;
static jmethodID constructortorId;
static jfieldID xId;
static jfieldID yId;
static jfieldID wId;
static jfieldID hId;
static jfieldID labelId;
static jfieldID probId;
static jfieldID msid;
static jfieldID objectsid;

// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_loadModel1(JNIEnv* env, jobject thiz, jstring name,jobjectArray list, jint modelid, jint cpugpu)
{

    jsize length = env->GetArrayLength(list);
    globalStringArray = (jobjectArray)env->NewGlobalRef(list);

//    convertJObjectArrayToCharArray(env,list,class_names);
    globalIntVariable = static_cast<int>(length);
    __android_log_print(ANDROID_LOG_DEBUG, "NcnnYolov8", "长度: %d",globalIntVariable);
    if (modelid < 0 || modelid > 6 || cpugpu < 0 || cpugpu > 1)
    {
        return JNI_FALSE;
    }

//    if (!isClass(env,"cheese/core/mark"))
//    {
//        return JNI_FALSE;
//    }


    const int target_sizes[] =
            {
                    320,
                    320,
            };

    const float mean_vals[][3] =
            {
                    {103.53f, 116.28f, 123.675f},
                    {103.53f, 116.28f, 123.675f},
            };

    const float norm_vals[][3] =
            {
                    { 1 / 255.f, 1 / 255.f, 1 / 255.f },
                    { 1 / 255.f, 1 / 255.f, 1 / 255.f },
            };


    int target_size = target_sizes[(int)modelid];
    bool use_gpu = (int)cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);

        if (use_gpu && ncnn::get_gpu_count() == 0)
        {
            // no gpu
            delete g_yolo;
            g_yolo = 0;
        }
        else
        {
            if (!g_yolo)
                g_yolo = new Yolo;


            g_yolo->load(env->GetStringUTFChars(name,0), target_size, mean_vals[(int)modelid], norm_vals[(int)modelid], use_gpu);

//            g_yolo->load("/storage/emulated/0/yolov8n.bin","/storage/emulated/0/yolov8n.param", modeltype, target_size, mean_vals[(int)modelid], norm_vals[(int)modelid], use_gpu);
        }
    }

    // init jni glue
    jclass localObjCls = env->FindClass("com/tencent/yolov8ncnn/Yolov8Ncnn$Obj");
    objCls = reinterpret_cast<jclass>(env->NewGlobalRef(localObjCls));

    constructortorId = env->GetMethodID(objCls, "<init>", "(Lcom/tencent/yolov8ncnn/Yolov8Ncnn;)V");

    xId = env->GetFieldID(objCls, "x", "F");
    yId = env->GetFieldID(objCls, "y", "F");
    wId = env->GetFieldID(objCls, "w", "F");
    hId = env->GetFieldID(objCls, "h", "F");
    labelId = env->GetFieldID(objCls, "label", "Ljava/lang/String;");
    probId = env->GetFieldID(objCls, "prob", "F");
    msid = env->GetFieldID(objCls, "speed", "F");
    objectsid = env->GetFieldID(objCls, "num", "F");


    return JNI_TRUE;
}


// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_loadModel(JNIEnv* env, jobject thiz, jobject assetManager, jstring name,jobjectArray list , jint modelid, jint cpugpu)
{
    jsize length = env->GetArrayLength(list);
    globalStringArray = (jobjectArray)env->NewGlobalRef(list);

//    const char* class_names[length];
//    convertJObjectArrayToCharArray(env,list,class_names);
    globalIntVariable = static_cast<int>(length);
    __android_log_print(ANDROID_LOG_DEBUG, "NcnnYolov8", "长度: %d",globalIntVariable);
    if (modelid < 0 || modelid > 6 || cpugpu < 0 || cpugpu > 1)
    {
        return JNI_FALSE;
    }

//    if (!isClass(env,"cheese/core/mark"))
//    {
//        return JNI_FALSE;
//    }


    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "loadModel %p", mgr);


    const int target_sizes[] =
            {
                    320,
                    320,
            };

    const float mean_vals[][3] =
            {
                    {103.53f, 116.28f, 123.675f},
                    {103.53f, 116.28f, 123.675f},
            };

    const float norm_vals[][3] =
            {
                    { 1 / 255.f, 1 / 255.f, 1 / 255.f },
                    { 1 / 255.f, 1 / 255.f, 1 / 255.f },
            };


    int target_size = target_sizes[(int)modelid];
    bool use_gpu = (int)cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);

        if (use_gpu && ncnn::get_gpu_count() == 0)
        {
            // no gpu
            delete g_yolo;
            g_yolo = 0;
        }
        else
        {
            if (!g_yolo)
                g_yolo = new Yolo;
            g_yolo->load(mgr, env->GetStringUTFChars(name,0), target_size, mean_vals[(int)modelid], norm_vals[(int)modelid], use_gpu);


        }
    }

    // init jni glue
    jclass localObjCls = env->FindClass("com/tencent/yolov8ncnn/Yolov8Ncnn$Obj");
    objCls = reinterpret_cast<jclass>(env->NewGlobalRef(localObjCls));

    constructortorId = env->GetMethodID(objCls, "<init>", "(Lcom/tencent/yolov8ncnn/Yolov8Ncnn;)V");

    xId = env->GetFieldID(objCls, "x", "F");
    yId = env->GetFieldID(objCls, "y", "F");
    wId = env->GetFieldID(objCls, "w", "F");
    hId = env->GetFieldID(objCls, "h", "F");
    labelId = env->GetFieldID(objCls, "label", "Ljava/lang/String;");
    probId = env->GetFieldID(objCls, "prob", "F");
    msid = env->GetFieldID(objCls, "speed", "F");
    objectsid = env->GetFieldID(objCls, "num", "F");

    return JNI_TRUE;
}

// public native boolean openCamera(int facing);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_openCamera(JNIEnv* env, jobject thiz, jint facing)
{
    if (facing < 0 || facing > 1)
        return JNI_FALSE;

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "openCamera %d", facing);

    g_camera->open((int)facing);

    return JNI_TRUE;
}

// public native boolean closeCamera();
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_closeCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "closeCamera");

    g_camera->close();

    return JNI_TRUE;
}

// public native boolean setOutputWindow(Surface surface);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_setOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "setOutputWindow %p", win);

    g_camera->set_window(win);

    return JNI_TRUE;
}




// public native Obj[] Detect(Bitmap bitmap);
JNIEXPORT jobjectArray JNICALL  Java_com_tencent_yolov8ncnn_Yolov8Ncnn_Detect(JNIEnv* env, jobject thiz,
                                                                              jobject bitmap)
{


    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
        return NULL;

    // Android Bitmap转ncnn::Mat
    ncnn::Mat in = ncnn::Mat::from_android_bitmap(env, bitmap, ncnn::Mat::PIXEL_RGB);
    double start_time = ncnn::get_current_time();
    std::vector<Object> objects;
    {
        ncnn::MutexLockGuard g(lock);

        if (g_yolo)
        {
            g_yolo->detect_mat(in, objects);
        }
    }
//    static const char* class_names[] = {
//            "person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light",
//            "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow",
//            "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee",
//            "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard",
//            "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple",
//            "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch",
//            "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard", "cell phone",
//            "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors", "teddy bear",
//            "hair drier", "toothbrush"
//    };
    jsize length = env->GetArrayLength(globalStringArray);
    __android_log_print(ANDROID_LOG_DEBUG, "NcnnYolov8", "objects num: %d", length);
    const char* class_names[length];
    convertJObjectArrayToCharArray(env,globalStringArray,class_names);
    // objects to Obj[]
//    for (int i = 0; i < length; i++) {
//        __android_log_print(ANDROID_LOG_INFO, "MyClass", "class_names[%d]: %s", i, class_names[i]);
//    }

    jobjectArray jObjArray = env->NewObjectArray(objects.size(), objCls, NULL);
//    env->SetFloatField(jObj, objectsid, objects.size());
    __android_log_print(ANDROID_LOG_DEBUG, "NcnnYolov8", "objects num: %d", objects.size());
//    jobject jObj;
    for (size_t i=0; i<objects.size(); i++)
    {
        jobject  jObj=   env->NewObject(objCls, constructortorId, thiz);

        env->SetFloatField(jObj, xId, objects[i].rect.x);
        env->SetFloatField(jObj, yId, objects[i].rect.y);
        env->SetFloatField(jObj, wId, objects[i].rect.width);
        env->SetFloatField(jObj, hId, objects[i].rect.height);
        env->SetObjectField(jObj, labelId, env->NewStringUTF(class_names[objects[i].label]));
        env->SetFloatField(jObj, probId, objects[i].prob);

        env->SetObjectArrayElement(jObjArray, i, jObj);
        __android_log_print(ANDROID_LOG_DEBUG, "NcnnYolov8", "%.2f  %.2f  %.2f  %.2f %s %.2f", \
        objects[i].rect.x, objects[i].rect.y, objects[i].rect.width, objects[i].rect.height, \
        class_names[objects[i].label],  objects[i].prob);
    }

    double elasped = ncnn::get_current_time() - start_time;
//    env->SetFloatField(jObj, msid, elasped);

    jclass javaClass = env->FindClass("com/tencent/yolov8ncnn/Yolov8Ncnn");
    if (javaClass != NULL) {
        jfieldID fieldId = env->GetStaticFieldID(javaClass, "speed", "D");

        if (fieldId != NULL) {
            env->SetStaticDoubleField(javaClass, fieldId, elasped);
        }

    }


    __android_log_print(ANDROID_LOG_DEBUG, "NcnnYolov8", "%.2fms  detect", elasped);


    return jObjArray;
}



}
