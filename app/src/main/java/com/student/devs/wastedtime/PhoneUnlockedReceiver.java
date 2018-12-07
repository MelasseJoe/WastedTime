package com.student.devs.wastedtime;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Détecte si le téléphone vient d'être dévérouillé
 */
public class PhoneUnlockedReceiver extends BroadcastReceiver {

    public static boolean unLocked = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.isKeyguardSecure()) {
            //phone was unlocked, do stuff here
            Log.d("Debug", "Phone unLocked");
            unLocked = true;
        }
    }
}
