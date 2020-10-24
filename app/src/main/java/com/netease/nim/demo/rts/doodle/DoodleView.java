package com.netease.nim.demo.rts.doodle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.netease.nim.demo.rts.doodle.action.Action;
import com.netease.nim.demo.rts.doodle.action.MyPath;

import java.util.ArrayList;
import java.util.List;

/**
 * 涂鸦板控件（基类）
 * <p/>
 * Created by huangjun on 2015/6/24.
 */
public class DoodleView extends SurfaceView implements SurfaceHolder.Callback, TransactionObserver {

    public enum Mode {
        PAINT,
        PLAYBACK,
        BOTH
    }

    private final String TAG = "DoodleView";

    private SurfaceHolder surfaceHolder;

    private DoodleChannel paintChannel; // 绘图通道

    private DoodleChannel playbackChannel; // 回放通道

    private TransactionManager transactionManager; // 数据发送管理器

    private int bgColor = Color.WHITE; // 背景颜色

    private float zoom = 1.0f; // 收发数据时缩放倍数（归一化）

    private float paintOffsetY = 0.0f; // 绘制时的Y偏移（去掉ActionBar,StatusBar,marginTop等高度）
    private float paintOffsetX = 0.0f; // 绘制事的X偏移（去掉marginLeft的宽度）

    private float lastX = 0.0f;
    private float lastY = 0.0f;

    public DoodleView(Context context) {
        super(context);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        this.setFocusable(true);
    }

    /**
     * 初始化（必须调用）
     *
     * @param mode    设置板书模式
     * @param bgColor 设置板书的背景颜色
     */
    public void init(String sessionId, String toAccount, Mode mode, int bgColor, Context context) {
        this.transactionManager = new TransactionManager(sessionId, toAccount, context);

        if (mode == Mode.PAINT || mode == Mode.BOTH) {
            this.paintChannel = new DoodleChannel();
        }

        if (mode == Mode.PLAYBACK || mode == Mode.BOTH) {
            this.playbackChannel = new DoodleChannel();
            this.transactionManager.registerTransactionObserver(this);
        }

        this.bgColor = bgColor;
    }

    public void onResume() {
        new Handler(getContext().getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    return;
                }
                drawHistoryActions(canvas);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }, 50);
    }

    /**
     * 退出涂鸦板时调用
     */
    public void end() {
        if (transactionManager != null) {
            transactionManager.end();
        }
    }

    /**
     * 设置绘制时画笔的偏移
     *
     * @param x DoodleView的MarginLeft的宽度
     * @param y ActionBar与StatusBar及DoodleView的MarginTop的高度的和
     */
    public void setPaintOffset(float x, float y) {
        this.paintOffsetX = x;
        this.paintOffsetY = y;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        onPaintBackground();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceView created, width = " + width + ", height = " + height);
        zoom = width;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * ******************************* 绘图板 ****************************
     */

    /**
     * 设置绘制时的画笔颜色
     *
     * @param color
     */
    public void setPaintColor(String color) {
        this.paintChannel.setColor(color);
    }

    /**
     * 设置回放时的画笔颜色
     *
     * @param color
     */
    public void setPlaybackColor(String color) {
        this.playbackChannel.setColor(color);
    }

    /**
     * 设置画笔的粗细
     *
     * @param size
     */
    public void setPaintSize(int size) {
        if (size > 0) {
            this.paintChannel.paintSize = size;
            this.playbackChannel.paintSize = size;
        }
    }

    /**
     * 设置当前画笔的形状
     *
     * @param type
     */
    public void setPaintType(int type) {
        this.paintChannel.setType(type);
        this.playbackChannel.setType(type);
    }

    /**
     * 设置当前画笔为橡皮擦
     *
     * @param size 橡皮擦的大小（画笔的粗细)
     */
    public void setEraseType(int size) {
        this.paintChannel.setEraseType(this.bgColor, size);
    }

    /**
     * 撤销一步
     *
     * @return 撤销是否成功
     */
    public synchronized boolean paintBack() {
        if (paintChannel == null) {
            return false;
        }

        boolean res = back(true);
        transactionManager.sendRevokeTransaction();
        return res;
    }

    /**
     *
     */
    public synchronized void clear() {
        clearAll();
        transactionManager.sendClearSelfTransaction();
    }

    /**
     * 触摸绘图
     *
     * @param event
     * @return
     */
    @Override
    public synchronized boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        float touchX = event.getRawX();
        float touchY = event.getRawY();
        touchX -= paintOffsetX;
        touchY -= paintOffsetY;
        Log.i(TAG, "x=" + touchX + ", y=" + touchY);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                onPaintActionStart(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                onPaintActionMove(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                onPaintActionEnd();
                break;
            default:
                break;
        }

        return true;
    }

    private void onPaintActionStart(float x, float y) {
        if (paintChannel == null) {
            return;
        }

        onActionStart(true, x, y);
        transactionManager.sendStartTransaction(x / zoom, y / zoom);
    }

    private void onPaintActionMove(float x, float y) {
        if (paintChannel == null) {
            return;
        }

        if (!isNewPoint(x, y)) {
            return;
        }

        onActionMove(true, x, y);
        transactionManager.sendMoveTransaction(x / zoom, y / zoom);
    }

    private void onPaintActionEnd() {
        if (paintChannel == null) {
            return;
        }

        onActionEnd(true);
        transactionManager.sendEndTransaction(lastX / zoom, lastY / zoom);
    }

    /**
     * ******************************* 回放板 ****************************
     */

    @Override
    public synchronized void onTransaction(List<Transaction> transactions) {
        Log.i(TAG, "onTransaction, size =" + transactions.size());

        if (playbackChannel == null) {
            return;
        }

        List<Transaction> cache = new ArrayList<>(transactions.size());
        for (Transaction t : transactions) {
            if (t == null) {
                continue;
            }

            if (t.isPaint()) {
                // 正常画笔
                cache.add(t);
            } else {
                onMultiTransactionsDraw(cache);
                cache.clear();
                if (t.isRevoke()) {
                    back(false);
                } else if (t.isClearSelf()) {
                    clearAll();
                    transactionManager.sendClearAckTransaction();
                } else if (t.isClearAck()) {
                    clearAll();
                }
            }
        }

        if (cache.size() > 0) {
            onMultiTransactionsDraw(cache);
            cache.clear();
        }
    }

    private void setPlaybackEraseType(int size) {
        this.playbackChannel.setEraseType(this.bgColor, size);
    }

    /**
     * ******************************* 基础绘图封装 ****************************
     */

    private void onPaintBackground() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(bgColor);  // 涂鸦板背景颜色
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void onActionStart(boolean isPaintView, float x, float y) {
        DoodleChannel channel = isPaintView ? paintChannel : playbackChannel;
        if (channel == null) {
            return;
        }

        channel.action = new MyPath(x, y, channel.paintColor, channel.paintSize);
    }

    private void onActionMove(boolean isPaintView, float x, float y) {
        DoodleChannel channel = isPaintView ? paintChannel : playbackChannel;
        if (channel == null) {
            return;
        }

        if (channel.action == null) {
            // 有可能action被清空，此时收到move，重新补个start
            onPaintActionStart(x, y);
        }

        Canvas canvas = surfaceHolder.lockCanvas();
        drawHistoryActions(canvas);
        // 绘制当前Action
        channel.action.onMove(x, y);
        channel.action.onDraw(canvas);
        if (canvas != null) {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void onActionEnd(boolean isPaintView) {
        DoodleChannel channel = isPaintView ? paintChannel : playbackChannel;
        if (channel == null || channel.action == null) {
            return;
        }

        channel.actions.add(channel.action);
        channel.action = null;
    }

    private void onMultiTransactionsDraw(List<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            return;
        }

        Canvas canvas = surfaceHolder.lockCanvas();
        drawHistoryActions(canvas);
        // 绘制新的数据
        for (Transaction t : transactions) {
            switch (t.getStep()) {
                case Transaction.ActionStep.START:
                    if (playbackChannel.action != null) {
                        // 如果没有收到end包，在这里补提交
                        playbackChannel.actions.add(playbackChannel.action);
                    }

                    playbackChannel.action = new MyPath(t.getX() * zoom, t.getY() * zoom, playbackChannel
                            .paintColor, playbackChannel.paintSize);
                    playbackChannel.action.onStart(canvas);
                    break;
                case Transaction.ActionStep.MOVE:
                    if (playbackChannel.action != null) {
                        playbackChannel.action.onMove(t.getX() * zoom, t.getY() * zoom);
                        playbackChannel.action.onDraw(canvas);
                    }
                    break;
                case Transaction.ActionStep.END:
                    if (playbackChannel.action != null) {
                        playbackChannel.actions.add(playbackChannel.action);
                        playbackChannel.action = null;
                    }
                    break;
                default:
                    break;
            }
        }
        if (canvas != null) {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawHistoryActions(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        // 绘制背景
        canvas.drawColor(bgColor);

        if (playbackChannel != null && playbackChannel.actions != null) {
            for (Action a : playbackChannel.actions) {
                a.onDraw(canvas);
            }

            // 绘制当前
            if (playbackChannel.action != null) {
                playbackChannel.action.onDraw(canvas);
            }
        }

        // 绘制所有历史Action
        if (paintChannel != null && paintChannel.actions != null) {
            for (Action a : paintChannel.actions) {
                a.onDraw(canvas);
            }

            // 绘制当前
            if (paintChannel.action != null) {
                paintChannel.action.onDraw(canvas);
            }
        }
    }

    private boolean back(boolean isPaintView) {
        DoodleChannel channel = isPaintView ? paintChannel : playbackChannel;
        if (channel == null) {
            return false;
        }

        if (channel.actions != null && channel.actions.size() > 0) {
            channel.actions.remove(channel.actions.size() - 1);
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) {
                return false;
            }
            drawHistoryActions(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
            return true;
        }
        return false;
    }

    private void clearAll() {
        clear(false);
        clear(true);
    }

    private void clear(boolean isPaintView) {
        DoodleChannel channel = isPaintView ? paintChannel : playbackChannel;
        if (channel == null) {
            return;
        }

        if (channel.actions != null) {
            channel.actions.clear();
        }
        channel.action = null;

        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        drawHistoryActions(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    private boolean isNewPoint(float x, float y) {
        if (Math.abs(x - lastX) <= 0.1f && Math.abs(y - lastY) <= 0.1f) {
            return false;
        }

        lastX = x;
        lastY = y;

        return true;
    }
}
