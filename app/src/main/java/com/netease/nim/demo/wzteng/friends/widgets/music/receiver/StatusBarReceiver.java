package com.netease.nim.demo.wzteng.friends.widgets.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.netease.nim.demo.wzteng.friends.widgets.music.service.PlayService;
import com.netease.nim.demo.wzteng.friends.widgets.music.utils.Actions;

public class StatusBarReceiver extends BroadcastReceiver {
    public static final String ACTION_STATUS_BAR = "STATUS_BAR_ACTIONS";
    public static final String EXTRA = "extra";
    public static final String EXTRA_NEXT = "next";
    public static final String EXTRA_PLAY_PAUSE = "play_pause";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }

        String extra = intent.getStringExtra(EXTRA);
        if (TextUtils.equals(extra, EXTRA_NEXT)) {
            PlayService.startCommand(context, Actions.ACTION_MEDIA_NEXT);
        } else if (TextUtils.equals(extra, EXTRA_PLAY_PAUSE)) {
            PlayService.startCommand(context, Actions.ACTION_MEDIA_PLAY_PAUSE);
        }
    }
}
