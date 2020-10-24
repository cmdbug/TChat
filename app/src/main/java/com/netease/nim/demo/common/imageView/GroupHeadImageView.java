package com.netease.nim.demo.common.imageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.netease.nim.demo.R;
import com.netease.nim.uikit.common.util.media.BitmapDecoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 群组头像合成控件
 * <p/>
 * Created by huangjun on 2015/6/17.
 */
public class GroupHeadImageView extends ImageView {

    public GroupHeadImageView(Context context) {
        super(context);
    }

    public GroupHeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupHeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static final int[] headIconResources = new int[]{
            R.drawable.head_icon_1, R.drawable.head_icon_2, R.drawable.head_icon_3
    };

    private static final Paint antiPaint = createAntiPaint();

    private static final Paint createAntiPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        return paint;
    }

    private static final Paint circlePaint = createCirclePaint();

    private static final Paint createCirclePaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        return paint;
    }

    private static final Paint destInPaint = createDestInPaint();

    private static final Paint createDestInPaint() {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        return paint;
    }

    private static final Paint maskPaint = createMaskPaint();

    private static final Paint createMaskPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        return paint;
    }

    private static final float o = 11f / 36f;
    private static final float i = 10f / 36f;

    private List<Bitmap> bitmaps;

    /**
     * 确定三个RECT(外圆，内圆)，左上角的起点
     */
    private static final float[][][] p = new float[][][]{
            new float[][]{
                    new float[]{0.5f - o, 0},
                    new float[]{0.5f - i, o - i},
            },
            new float[][]{
                    new float[]{1.0f - o * 2, 1.0f - o * 2},
                    new float[]{1.0f - o * 2 + o - i, 1.0f - o * 2 + o - i},
            },
            new float[][]{
                    new float[]{0, 1.0f - o * 2},
                    new float[]{o - i, 1.0f - o * 2 + o - i},
            },

    };

    public void loadResourceImage() {
        int width = getWidth();
        int height = getHeight();

        this.bitmaps = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            bitmaps.add(BitmapDecoder.decodeSampled(getResources(), headIconResources[i], width, height));
        }

        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        // bounds
        int width = getWidth();
        int height = getHeight();

        super.draw(canvas);

        // to canvas
        join3(canvas, width, height, this.bitmaps);
    }

    private final void join3(Canvas canvas, int destWidth, int destHeight, List<Bitmap> bitmaps) {
        if (bitmaps == null) {
            return;
        }

        int size = Math.min(p.length, bitmaps.size());
        for (int index = size - 1; index >= 0; index--) {
            Bitmap bitmap = bitmaps.get(index);

            // 矩形区域
            float[] posOut = new float[]{
                    p[index][0][0] * destWidth, p[index][0][1] * destHeight
            };

            // 外圈内圈半径
            float rdsOut = destWidth * o;
            float rdsIn = destWidth * i;

            // 头像层
            RectF rect = new RectF(posOut[0], posOut[1], posOut[0] + 2 * rdsOut, posOut[1] + 2 * rdsOut);
            canvas.saveLayer(rect, null, Canvas.ALL_SAVE_FLAG);
            // 对头像位图进行缩放
            Matrix matrix = new Matrix();
            matrix.postScale((float) destWidth / bitmap.getWidth(), (float) destHeight / bitmap.getHeight());
            matrix.postScale(2 * o, 2 * o);

            canvas.translate(posOut[0], posOut[1]);
            canvas.drawBitmap(bitmap, matrix, antiPaint);
            canvas.translate(-posOut[0], -posOut[1]);

            Paint paint = circlePaint;
            paint.setStrokeWidth(rdsOut - rdsIn);
            canvas.drawCircle(posOut[0] + rdsOut, posOut[1] + rdsOut, rdsOut, paint);

            // MASK层
            canvas.saveLayer(rect, destInPaint, Canvas.ALL_SAVE_FLAG);
            canvas.drawCircle(posOut[0] + rdsOut, posOut[1] + rdsOut, rdsOut, maskPaint);

            // MASK层与头像层合并->MERGE层
            canvas.restore();

            // MERGE层与DEFAULT层合并
            canvas.restore();
        }
    }
}
