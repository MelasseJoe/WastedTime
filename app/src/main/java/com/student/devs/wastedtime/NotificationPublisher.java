package com.student.devs.wastedtime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {

    Intent it;
    Context ct;

    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("perso", Context.MODE_PRIVATE);
        String packageNamePresent = preferences.getString("packageNameStart","");

        ct = context;
        it = intent;

        Log.d("PackageDebug", "Notification publisher : " + it.getStringExtra("package"));

        if(!packageNamePresent.equals(intent.getStringExtra("package"))) {
            showNotification(context, "Notification", intent.getStringExtra("corps"), 1, intent, intent.getStringExtra("package"));
        }
    }

    public void showNotification(Context context, String title, String body, int notificationId, Intent intent, String package_name) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);

        /*PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );*/

        Intent i = new Intent(context, question_activity.class);
        i.putExtra("package", intent.getStringExtra("package"));

        Log.d("timeDiff", "send " + String.valueOf(intent.getLongExtra("timeDiff",-1)));

        i.putExtra("timeDiff", intent.getLongExtra("timeDiff",-1));

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }
}