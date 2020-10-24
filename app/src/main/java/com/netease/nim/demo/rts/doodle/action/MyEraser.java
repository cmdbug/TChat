package com.netease.nim.demo.rts.doodle.action;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 橡皮擦（与画布背景色相同的Path）
 * <p/>
 * Created by Administrator on 2015/6/24.
 */
public class MyEraser extends Action {
    private Path path;

    public MyEraser(Float x, Float y, Integer color, Integer size) {
        super(x, y, color, size);
        path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y);
    }

    @Override
    public boolean isSequentialAction() {
        return true;
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path, paint);
    }

    public void onMove(float mx, float my) {
        path.lineTo(mx, my);
    }
}
