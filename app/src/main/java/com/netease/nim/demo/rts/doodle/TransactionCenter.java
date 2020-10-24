package com.netease.nim.demo.rts.doodle;

import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.rts.RTSManager;
import com.netease.nimlib.sdk.rts.model.RTSTunData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 白板数据收发中心
 * <p/>
 * Created by huangjun on 2015/6/29.
 */
public class TransactionCenter {

    private int index = 0;

    private final String TAG = "TransactionCenter";

    // sessionId to TransactionObserver
    private Map<String, TransactionObserver> observers = new HashMap<>(2);

    public static TransactionCenter getInstance() {
        return TransactionCenterHolder.instance;
    }

    private static class TransactionCenterHolder {
        public static final TransactionCenter instance = new TransactionCenter();
    }

    public void registerObserver(String sessionId, TransactionObserver o) {
        this.observers.put(sessionId, o);
    }

    /**
     * 数据发送
     */
    public void sendToRemote(String sessionId, String toAccount, List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        String data = pack(transactions);
        try {
            RTSTunData channelData = new RTSTunData(sessionId, toAccount, data.getBytes
                    ("UTF-8"), data.getBytes().length);
            RTSManager.getInstance().sendData(channelData);
            Log.i(TAG, "SEND DATA = " + index + ", BYTES = " + data.getBytes().length);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e("Transaction", "send to remote, getBytes exception : " + data);
        }
    }

    private String pack(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactions) {
            sb.append(Transaction.pack(t));
        }

        // 打入序号
        sb.append(Transaction.packIndex(++index));

        return sb.toString();
    }

    /**
     * 数据接收
     */
    public void onReceive(String sessionId, String data) {
        if (observers.containsKey(sessionId)) {
            observers.get(sessionId).onTransaction(unpack(data));
        }
    }

    private List<Transaction> unpack(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        List<Transaction> transactions = new ArrayList<>();
        String[] pieces = data.split(";");
        for (String p : pieces) {
            Transaction t = Transaction.unpack(p);
            if (t != null) {
                transactions.add(t);
            }
        }

        return transactions;
    }
}
