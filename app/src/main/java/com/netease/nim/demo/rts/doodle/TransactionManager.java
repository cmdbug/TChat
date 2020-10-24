package com.netease.nim.demo.rts.doodle;

import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction发包管理器
 * <p/>
 * Created by huangjun on 2015/6/24.
 */
class TransactionManager {

    private final int TIMER_TASK_PERIOD = 30;

    private String sessionId;

    private String toAccount;

    private Handler handler;

    private List<Transaction> cache = new ArrayList<>(1000);

    public TransactionManager(String sessionId, String toAccount, Context context) {
        this.sessionId = sessionId;
        this.toAccount = toAccount;
        this.handler = new Handler(context.getMainLooper());
        this.handler.postDelayed(timerTask, TIMER_TASK_PERIOD); // 立即开启定时器
    }

    public void end() {
        this.handler.removeCallbacks(timerTask);
    }

    public void registerTransactionObserver(TransactionObserver o) {
        TransactionCenter.getInstance().registerObserver(sessionId, o);
    }

    public void sendStartTransaction(float x, float y) {
        cache.add(new Transaction().makeStartTransaction(x, y));
    }

    public void sendMoveTransaction(float x, float y) {
        cache.add(new Transaction().makeMoveTransaction(x, y));
    }

    public void sendEndTransaction(float x, float y) {
        cache.add(new Transaction().makeEndTransaction(x, y));
    }

    public void sendRevokeTransaction() {
        cache.add(new Transaction().makeRevokeTransaction());
    }

    public void sendClearSelfTransaction() {
        cache.add(new Transaction().makeClearSelfTransaction());
    }

    public void sendClearAckTransaction() {
        cache.add(new Transaction().makeClearAckTransaction());
    }

    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(timerTask);
            {
                if (cache.size() > 0) {
                    sendCacheTransaction();
                }
            }
            handler.postDelayed(timerTask, TIMER_TASK_PERIOD);
        }
    };

    private void sendCacheTransaction() {
        TransactionCenter.getInstance().sendToRemote(sessionId, toAccount, this.cache);
        cache.clear();
    }
}
