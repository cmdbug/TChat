#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_netease_nim_demo_main_activity_WelcomeActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello by WZTENG";
    return env->NewStringUTF(hello.c_str());
}
