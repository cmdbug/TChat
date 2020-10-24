package com.netease.nim.demo.avchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * 来电状态监听
 * <p/>
 * Created by huangjun on 2015/5/13.
 */
public class IncomingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
            final String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            PhoneCallStateObserver.getInstance().onCallStateChanged(state);
        }
    }
}
