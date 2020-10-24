package com.netease.nim.demo.jsbridge;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.netease.nim.demo.R;
import com.netease.nim.demo.main.activity.MainActivity;

/**
 * Created by hzliuxuanlin on 16/10/22.
 */
public class NotificationHelper {
    private Context context;

    private NotificationManager notificationManager;
    private Notification notification;
    private static final int NOTIFY_ID = NotificationHelper.class.hashCode();

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void activeCallingNotification(boolean active, String msg) {
        if (notificationManager != null) {
            if (active) {
                buildCallingNotification(msg);
                notificationManager.notify(NOTIFY_ID, notification);
            } else {
                notificationManager.cancel(NOTIFY_ID);
            }
        }
    }

    private void buildCallingNotification(String msg) {
        if (notification == null) {
            Intent localIntent = new Intent();
            localIntent.setClass(context, MainActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            int iconId = R.drawable.ic_logo;

            PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFY_ID, localIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
            notification = makeNotification(pendingIntent, context.getString(R.string.app_name), msg, msg, iconId, false, false);
        }
    }

    private Notification makeNotification(PendingIntent pendingIntent, String title, String content, String tickerText,
                                          int iconId, boolean ring, boolean vibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(tickerText)
                .setSmallIcon(iconId);
        int defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (ring) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        builder.setDefaults(defaults);

        return builder.build();
    }
}
