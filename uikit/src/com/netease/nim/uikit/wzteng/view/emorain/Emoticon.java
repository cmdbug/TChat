package com.netease.nim.uikit.wzteng.view.emorain;

import android.graphics.Bitmap;

/**
 * 表情雨里的表情图
 */
public class Emoticon {
    //出现的时间时间戳
    private int appearTimestamp;
    //对应的Bitmap
    private Bitmap bitmap;
    //随机缩放比例
    private float scale;
    //位置
    private int x, y;
    //位移速度
    private int velocityX, velocityY;

    private Emoticon(Builder builder) {
        appearTimestamp = builder.appearTimestamp;
        bitmap = builder.bitmap;
        scale = builder.scale;
        setX(builder.x);
        setY(builder.y);
        velocityX = builder.velocityX;
        velocityY = builder.velocityY;
    }

    public int getAppearTimestamp() {
        return appearTimestamp;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getScale() {
        return scale;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static final class Builder {
        private int appearTimestamp;
        private Bitmap bitmap;
        private float scale;
        private int x;
        private int y;
        private int velocityX;
        private int velocityY;

        public Builder() {
        }

        public Builder appearTimestamp(int val) {
            appearTimestamp = val;
            return this;
        }

        public Builder bitmap(Bitmap val) {
            bitmap = val;
            return this;
        }

        public Builder scale(float val) {
            scale = val;
            return this;
        }

        public Builder x(int val) {
            x = val;
            return this;
        }

        public Builder y(int val) {
            y = val;
            return this;
        }

        public Builder velocityX(int val) {
            velocityX = val;
            return this;
        }

        public Builder velocityY(int val) {
            velocityY = val;
            return this;
        }

        public Emoticon build() {
            return new Emoticon(this);
        }
    }
}
