package com.netease.nim.demo.rts.doodle;

import android.graphics.Color;

import com.netease.nim.demo.rts.doodle.action.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * 涂鸦板通道（输入通道，输出通道）
 * <p/>
 * Created by huangjun on 2015/6/29.
 */
class DoodleChannel {
    /**
     * 当前所选的画笔
     */
    public int type = 0; // 当前的形状类型

    public Action action; // 当前的形状对象

    public int paintColor = Color.BLACK;

    public int paintSize = 5;

    public int lastPaintColor = paintColor; // 上一次使用的画笔颜色（橡皮擦切换回形状时，恢复上次的颜色）

    public int lastPaintSize = paintSize; // 上一次使用的画笔粗细（橡皮擦切换回形状时，恢复上次的粗细）

    /**
     * 记录所有形状的列表
     */
    public List<Action> actions = new ArrayList<>();

    /**
     * 设置当前画笔的形状
     *
     * @param type
     */
    public void setType(int type) {
        if (this.type == SupportActionType.getInstance().getEraserType()) {
            // 从橡皮擦切换到某种形状，恢复画笔颜色，画笔粗细
            this.paintColor = this.lastPaintColor;
            this.paintSize = this.lastPaintSize;
        }

        this.type = type;
    }

    /**
     * 设置当前画笔为橡皮擦
     */
    public void setEraseType(int bgColor, int size) {
        this.type = SupportActionType.getInstance().getEraserType();
        this.lastPaintColor = this.paintColor; // 备份当前的画笔颜色
        this.lastPaintSize = this.paintSize; // 备份当前的画笔粗细
        this.paintColor = bgColor;
        if (size > 0) {
            paintSize = size;
        }
    }

    /**
     * 设置当前画笔的颜色
     *
     * @param color
     */
    public void setColor(String color) {
        if (this.type == SupportActionType.getInstance().getEraserType()) {
            // 如果正在使用橡皮擦，那么不能更改画笔颜色
            return;
        }

        this.paintColor = Color.parseColor(color);
    }

    /**
     * 设置画笔的粗细
     *
     * @param size
     */
    public void setSize(int size) {
        if (size > 0) {
            this.paintSize = size;
        }
    }
}
