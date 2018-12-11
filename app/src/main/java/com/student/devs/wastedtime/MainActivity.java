package com.student.devs.wastedtime;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Calendar;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private EditText mEditPackage;
    private EditText mEditClass;
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //permet de détecter quand le téléphone est Locked ou unLocked
        startService(new Intent(MainActivity.this, UpdateService.class));

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("perso", Context.MODE_PRIVATE);
        String packageNamePresent = getPackageName();
        long currentTime = Calendar.getInstance().getTime().getTime();

        preferences.edit().putBoolean("HaveBeenLocked",false).apply();
        preferences.edit().putLong("timePrev", -1).apply();
        preferences.edit().putString("packageNamePrev", "").apply();
        preferences.edit().putLong("timeStart", currentTime).apply();
        preferences.edit().putString("packageNameStart", packageNamePresent).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem switchItem = menu.findItem(R.id.toggleservice);
        mSwitch = (Switch) switchItem.getActionView();
        // First time will initialize with default value
        boolean isSavedAsChecked = Utils.getSharedPref(this, R.string.sharedprefs_key1, false);
        mSwitch.setChecked(isSavedAsChecked);
        mSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            // Save new value
            Utils.setSharedPref(MainActivity.this, R.string.sharedprefs_key1, isChecked);
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void onClick(View v) {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        //showNotification(getApplicationContext(),"Attention !","Vous venez de fermer \" WastedTime\"",1,new Intent());
        super.onDestroy();
    }

    /**
     * Permet d'envoyer une notification à l'utilisateur
     * @param context
     * @param title
     * @param body
     * @param notificationId
     * @param intent
     */
    public void showNotification(Context context, String title, String body, int notificationId, Intent intent) {
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

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
