package com.netease.nim.demo.wzteng.friends.widgets.music.executor;

public interface IExecutor<T> {
    void execute();

    void onPrepare();

    void onExecuteSuccess(T t);

    void onExecuteFail(Exception e);
}
