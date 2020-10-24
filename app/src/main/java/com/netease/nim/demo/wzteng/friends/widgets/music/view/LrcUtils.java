package com.netease.nim.demo.wzteng.friends.widgets.music.view;

import android.text.format.DateUtils;

import java.util.Locale;

public class LrcUtils {

    public static String formatTime(long milli) {
        int m = (int) (milli / DateUtils.MINUTE_IN_MILLIS);
        int s = (int) ((milli / DateUtils.SECOND_IN_MILLIS) % 60);
        String mm = String.format(Locale.getDefault(), "%02d", m);
        String ss = String.format(Locale.getDefault(), "%02d", s);
        return mm + ":" + ss;
    }
}
