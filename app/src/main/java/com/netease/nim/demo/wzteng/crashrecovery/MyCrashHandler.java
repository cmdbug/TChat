package com.netease.nim.demo.wzteng.crashrecovery;

import android.util.Log;

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    public MyCrashHandler() {
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("wzt", "myCrashHandler...");
        mUncaughtExceptionHandler.uncaughtException(t, e);
    }

    public static void register() {
        Thread.setDefaultUncaughtExceptionHandler(new MyCrashHandler());
    }
}
