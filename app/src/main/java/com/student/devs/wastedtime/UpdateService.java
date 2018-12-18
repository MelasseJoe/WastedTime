package com.student.devs.wastedtime;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Thibaut on 10/12/2018.
 */

public class UpdateService extends Service {

    BroadcastReceiver mReceiver;
    public static int countOn = 0;
    public static int countOff = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        Log.i("SCREEN", "Started");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_ANSWER);
        mReceiver = new PhoneLockedReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(mReceiver);
        Log.i("SCREEN", "Killed");
        Intent broadcastIntent = new Intent(this, PhoneLockedReceiver.class);
        broadcastIntent.putExtra("service", true);
        startActivity(broadcastIntent);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}