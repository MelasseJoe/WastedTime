package com.student.devs.wastedtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Thibaut on 10/12/2018.
 */


public class PhoneLockedReceiver extends BroadcastReceiver {
    private boolean screenOff;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED");

            Intent i = new Intent(context, UpdateService.class);
            context.startService(i);
        }
    }

}

