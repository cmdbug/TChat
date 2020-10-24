package com.netease.nim.demo.rts.doodle.action;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * 形状基类，所有涂鸦板上的绘制的形状继承该基类
 */
public abstract class Action {
    protected float startX;
    protected float startY;
    protected float stopX;
    protected float stopY;
    protected int color = Color.BLACK;
    protected int size;

    Action(float startX, float startY, int color, int size) {
        this.startX = startX;
        this.startY = startY;
        this.stopX = startX;
        this.stopY = startY;
        this.color = color;
        this.size = size;
    }

    /**
     * 是否是连续的图形（onMove所有帧都要画上）
     * 连续的：Path
     * 非连续的：Rect,Circle
     */
    public boolean isSequentialAction() {
        return false;
    }

    /**
     * ACTION_DOWN事件触发时调用（一个新的形状开始时，构造ACTION之后调用）
     * 某些图形在ACTION_DOWN时需要绘制，重载此方法
     *
     * @param canvas
     */
    public void onStart(Canvas canvas) {
    }

    /**
     * ACTION_MOVE事件触发时调用
     *
     * @param mx 当前MOVE到的手指位置x
     * @param my 当前MOVE到的手指位置y
     */
    public abstract void onMove(float mx, float my);

    /**
     * ACTION_MOVE过程或者ACTION_UP后形状的绘制
     *
     * @param canvas
     */
    public abstract void onDraw(Canvas canvas);
}