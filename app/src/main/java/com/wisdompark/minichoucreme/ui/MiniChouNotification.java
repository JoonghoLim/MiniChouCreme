package com.wisdompark.minichoucreme.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.wisdompark.minichoucreme.R;

public class MiniChouNotification {

    NotificationManager manager;
    Context mContext;
    public MiniChouNotification(Context context) {
        mContext = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showNotification(String title, String desc) {

        String channelId = "task_channel";
        String channelName = "task_name";
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_choux_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX) //최대로 펼침
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        manager.notify(77, builder.build());

    }
}
