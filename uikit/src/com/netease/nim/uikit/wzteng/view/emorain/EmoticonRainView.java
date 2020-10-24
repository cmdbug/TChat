package com.netease.nim.uikit.wzteng.view.emorain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 表情雨View
 */
public class EmoticonRainView extends View {
    //表情大小
    private static final int DEFAULT_EMOTICON_VIEW_SIZE_PIXEL = 100;
    //表情出现时的左右边距
    private static final int DEFAULT_START_X_PADDING_DP_VALUE = 15;
    //两个表情出现的最大间隔时间
    private static final int DEFAULT_APPEAR_INTERVAL_MAX_TIME = 250;
    //第一个表情出现到最后一个表情出现的时长
    private static final int DEFAULT_APPEAR_DURATION = 2000;
    //表情下落的最小、最大时长
    private static final int DEFAULT_RAIN_MAX_DURATION = 2500;
    private static final int DEFAULT_RAIN_MIN_DURATION = 2000;
    //表情最小、最大缩放百分比
    private static final int DEFAULT_SCALE_MIN_PERCENT = 80;
    private static final int DEFAULT_SCALE_MAX_PERCENT = 100;

    //是否自动回收bitmap
    private boolean autoRecycleBitmap = true;
    //是否正在下表情雨
    private boolean isRaining;
    //表情图高度
    private float mEmoticonHeight;
    private float mEmoticonWidth;

    private Random mRandom;
    private Matrix matrix;
    private Paint mPaint;

    //表情雨开始的时间戳
    private long mStartTimestamp;
    //x轴起始左右padding
    private int mXStartPadding;

    private final List<Bitmap> mBaseBitmaps = new ArrayList<>();
    private final List<Emoticon> mEmoticonList = new ArrayList<>();

    public EmoticonRainView(Context context) {
        super(context);
        init();
    }

    public EmoticonRainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmoticonRainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(GONE);

        setWillNotDraw(false);

        initValue();

        initPaint();
    }

    private void initValue() {
        mRandom = new Random();
        mXStartPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_START_X_PADDING_DP_VALUE, getContext().getResources().getDisplayMetrics());
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        matrix = new Matrix();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isRaining) {
            return;
        }
        long currentTimestamp = System.currentTimeMillis();
        boolean isDrawSuccess = false;
        long totalTimeInterval = currentTimestamp - mStartTimestamp;
        if (mEmoticonList.size() > 0) {
            for (int i = 0; i < mEmoticonList.size(); i++) {
                Emoticon emoticon = mEmoticonList.get(i);
                Bitmap bitmap = emoticon.getBitmap();
                if (bitmap.isRecycled() || isOutOfBottomBound(i) || totalTimeInterval < emoticon.getAppearTimestamp()) {
                    continue;
                }
                isDrawSuccess = true;

                matrix.reset();

                float heightScale = mEmoticonHeight / bitmap.getHeight();
                float widthScale = mEmoticonWidth / bitmap.getWidth();
                matrix.setScale(widthScale * emoticon.getScale(), heightScale * emoticon.getScale());

                emoticon.setX(emoticon.getX() + emoticon.getVelocityX());
                emoticon.setY(emoticon.getY() + emoticon.getVelocityY());

                matrix.postTranslate(emoticon.getX(), emoticon.getY());

                canvas.drawBitmap(bitmap, matrix, mPaint);
            }
        }
        if (!isDrawSuccess) {
            stop();
        } else {
            postInvalidate();
        }
    }

    /**
     * 某张图的位置是否超出下边界
     */
    private boolean isOutOfBottomBound(int position) {
        return mEmoticonList.get(position).getY() > getHeight();
    }

    /**
     * 开始动画
     */
    public void start(final Conf conf) {
        if (conf == null || conf.bitmaps == null || conf.bitmaps.size() == 0) {
            throw new RuntimeException("EmoticonRainView conf is error!");
        }
        stop();
        setVisibility(VISIBLE);

        post(new Runnable() {
            @Override
            public void run() {
                initAndResetData(conf);
                isRaining = true;
                invalidate();
            }
        });
    }

    /**
     * 初始化并重置数据
     */
    private void initAndResetData(Conf conf) {
        if (conf == null) {
            return;
        }
        this.mEmoticonHeight = conf.emoticonHeightPixel;
        this.mEmoticonWidth = conf.emoticonWidthPixel;
        this.mStartTimestamp = System.currentTimeMillis();

        mBaseBitmaps.clear();
        mBaseBitmaps.addAll(conf.bitmaps);
        mEmoticonList.clear();

        //开始画表情的总时间
        int currentDuration = 0;
        float range = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics());
        int type = mBaseBitmaps.size();
        int i = 0;
        while (currentDuration < DEFAULT_APPEAR_DURATION) {
            Emoticon.Builder emoticonBuilder = new Emoticon.Builder().bitmap(mBaseBitmaps.get(i % type));
            float scale = 1f * (mRandom.nextInt(DEFAULT_SCALE_MAX_PERCENT - DEFAULT_SCALE_MIN_PERCENT + 1) + DEFAULT_SCALE_MIN_PERCENT) / 100;
            emoticonBuilder.scale(scale).x(mRandom.nextInt(getWidth() - (int) (mEmoticonWidth * scale) - mXStartPadding * 2) + mXStartPadding);
            int y = (int) -Math.ceil(mEmoticonHeight * scale);
            emoticonBuilder.y(y);

            float height = getHeight() + (-y);
            float duration = (mRandom.nextInt(DEFAULT_RAIN_MAX_DURATION - DEFAULT_RAIN_MIN_DURATION + 1) + DEFAULT_RAIN_MIN_DURATION);
            int velocityY = (int) (height * 16 / duration);  //下落速度(pixel/16ms)
            emoticonBuilder.velocityY(velocityY == 0 ? 1 : velocityY);
            emoticonBuilder.velocityX(Math.round(mRandom.nextFloat() * range - range / 2f));
            emoticonBuilder.appearTimestamp(currentDuration);
            mEmoticonList.add(emoticonBuilder.build());

            currentDuration += mRandom.nextInt(DEFAULT_APPEAR_INTERVAL_MAX_TIME);
            i++;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //停止
        stop();
    }

    /**
     * 停止并考虑回收
     */
    public void stop() {
        isRaining = false;
        setVisibility(GONE);
        if (mBaseBitmaps != null && autoRecycleBitmap) {
            for (Bitmap mEmoticonBitmap : mBaseBitmaps) {
                if (!mEmoticonBitmap.isRecycled()) {
                    mEmoticonBitmap.recycle();
                }
            }
        }
    }

    /**
     * 设置控件是否自动回收传入的bitmaps
     */
    public void setAutoRecycleBitmap(boolean autoRecycleBitmap) {
        this.autoRecycleBitmap = autoRecycleBitmap;
    }

    /**
     * 配置
     */
    public static class Conf {
        //表情bitmap，虽然这里可以指定宽高，但是仍然需要先手动压缩一下bitmap，避免OOM
        private List<Bitmap> bitmaps;
        private int emoticonHeightPixel;
        private int emoticonWidthPixel;

        private Conf(Builder builder) {
            bitmaps = builder.bitmaps;
            emoticonHeightPixel = builder.emoticonHeightPixel;
            emoticonWidthPixel = builder.emoticonWidthPixel;
        }

        public static final class Builder {
            private List<Bitmap> bitmaps;
            private int emoticonHeightPixel;
            private int emoticonWidthPixel;

            public Builder() {
            }

            public Builder bitmaps(List<Bitmap> val) {
                bitmaps = val;
                return this;
            }

            public Builder emoticonHeightPixel(int val) {
                emoticonHeightPixel = val;
                return this;
            }

            public Builder emoticonWidthPixel(int val) {
                emoticonWidthPixel = val;
                return this;
            }

            public Conf build() {
                if (emoticonHeightPixel <= 0) {
                    emoticonHeightPixel = DEFAULT_EMOTICON_VIEW_SIZE_PIXEL;
                }
                if (emoticonWidthPixel <= 0) {
                    emoticonWidthPixel = DEFAULT_EMOTICON_VIEW_SIZE_PIXEL;
                }
                return new Conf(this);
            }
        }
    }
}
