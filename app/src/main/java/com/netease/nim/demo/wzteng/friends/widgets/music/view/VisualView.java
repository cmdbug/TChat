package com.netease.nim.demo.wzteng.friends.widgets.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by WZTENG on 2018/1/26 0026.
 */

public class VisualView extends View {

    private static final int DN_W = 470;//view宽度与单个音频块占比 - 正常480 需微调
    private static final int DN_H = 360;//view高度与单个音频块占比
    private static final int DN_SL = 15;//单个音频块宽度
    private static final int DN_SW = 5;//单个音频块高度

    private int hgap = 0;
    private int vgap = 0;
    private int levelStep = 230 / MAX_LEVEL;
    private float strokeWidth = 0;
    private float strokeLength = 0;

    protected final static int MAX_LEVEL = 30;//音量柱·音频块 - 最大个数
    protected final static int CYLINDER_NUM = 26;//音量柱 - 最大个数
    protected Paint mPaint = null;//画笔
    protected byte[] mData = new byte[CYLINDER_NUM];//音量柱 数组
    boolean mDataEn = true;

    public VisualView(Context context) {
        this(context, null);
    }

    public VisualView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public VisualView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();//初始化画笔工具
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(Color.WHITE);//画笔颜色
        mPaint.setStrokeJoin(Paint.Join.ROUND); //频块圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND); //频块圆角
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        float w, h, xr, yr;

        w = right - left;
        h = bottom - top;
        xr = w / (float) DN_W;
        yr = h / (float) DN_H;

        strokeWidth = DN_SW * yr;
        strokeLength = DN_SL * xr;
//        hgap = (int) ((w - strokeLength * CYLINDER_NUM) / (CYLINDER_NUM + 1));
        hgap = (int) ((w - strokeLength * CYLINDER_NUM) / (CYLINDER_NUM + 1) + 0.5f);
        vgap = (int) (h / (MAX_LEVEL + 2));//频谱块高度

        mPaint.setStrokeWidth(strokeWidth); //设置频谱块宽度
    }

    //绘制频谱块和倒影
    protected void drawCylinder(Canvas canvas, float x, byte value) {
        if (value == 0) {
            value = 1;
        }//最少有一个频谱块
        x = x - 6;
        for (int i = 0; i < value * 2 - 1; i++) { //每个能量柱绘制value个能量块
            float y = (getHeight() - i * vgap - vgap);//计算y轴坐标
            float y1 = (getHeight() + i * vgap + vgap);
            //绘制频谱块
//            mPaint.setColor(Color.argb(255, 255, 245, 220));//画笔颜色
            mPaint.setColor(blendColors(Color.argb(255, 255, 80, 0),
                    Color.argb(255, 255, 245, 220),
                    i / (float) (CYLINDER_NUM * 2)));
            canvas.drawLine(x, y, (x + strokeLength), y, mPaint);//绘制频谱块
        }

//        if (value == 0) {
//            value = 1;
//        }//最少有一个频谱块
//        for (int i = 0; i < value; i++) { //每个能量柱绘制value个能量块
//            float y = (getHeight() / 2 - i * vgap - vgap);//计算y轴坐标
//            float y1 = (getHeight() / 2 + i * vgap + vgap);
//            //绘制频谱块
//            mPaint.setColor(Color.WHITE);//画笔颜色
//            canvas.drawLine(x, y, (x + strokeLength), y, mPaint);//绘制频谱块
//
//            //绘制音量柱倒影
//            if (i <= 6 && value > 0) {
//                mPaint.setColor(Color.WHITE);//画笔颜色
//                mPaint.setAlpha(100 - (100 / 6 * i));//倒影颜色
//                canvas.drawLine(x, y1, (x + strokeLength), y1, mPaint);//绘制频谱块
//            }
//        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        int j = -4;
        for (int i = 0; i < CYLINDER_NUM / 2 - 4; i++) { //绘制能量柱
            drawCylinder(canvas, strokeWidth / 2 + hgap + i * (hgap + strokeLength), mData[i]);
        }
        for (int i = CYLINDER_NUM; i >= CYLINDER_NUM / 2 - 3; i--) {
            j++;
            drawCylinder(canvas, strokeWidth / 2 + hgap + (CYLINDER_NUM / 2 + j - 1) * (hgap + strokeLength), mData[i - 1]);
        }
    }

    //快速傅里叶变换
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        byte[] model = new byte[fft.length / 2 + 1];
        if (mDataEn) {
            model[0] = (byte) Math.abs(fft[1]);
            int j = 1;
            for (int i = 2; i < fft.length; ) {
                model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
                i += 2;
                j++;
            }
        } else {
            for (int i = 0; i < CYLINDER_NUM; i++) {
                model[i] = 0;
            }
        }
        for (int i = 0; i < CYLINDER_NUM; i++) {
            final byte a = (byte) (Math.abs(model[CYLINDER_NUM - i]) / levelStep);

            final byte b = mData[i];
            if (a > b) {
                mData[i] = a;
            } else {
                if (b > 0) {
                    mData[i]--;
                }
            }
        }
        postInvalidate();//刷新界面
    }

    //波形数据
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

    }

    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        float a = (Color.alpha(color1) * ratio) + (Color.alpha(color2) * inverseRation);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

}
