package com.netease.nim.demo.wzteng.friends.widgets.music.http;

public abstract class HttpCallback<T> {
    public abstract void onSuccess(T t);

    public abstract void onFail(Exception e);

    public void onFinish() {
    }
}
