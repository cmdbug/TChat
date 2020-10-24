package com.netease.nim.demo.rts.doodle.action;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 点
 * <p/>
 * Created by Administrator on 2015/6/24.
 */
public class MyPoint extends Action {
    public MyPoint(Float x, Float y, Integer color, Integer size) {
        super(x, y, color, size);
    }

    @Override
    public void onStart(Canvas canvas) {
        super.onStart(canvas);
        onDraw(canvas);
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(size);

        canvas.drawPoint(startX, startY, paint);
    }

    @Override
    public void onMove(float mx, float my) {

    }
}
