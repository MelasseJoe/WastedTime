package com.student.devs.wastedtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

/**
 * Created by Thibaut on 10/12/2018.
 */


public class PhoneLockedReceiver extends BroadcastReceiver {
    private boolean screenOff;

    Context ct;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent background = new Intent(context, UpdateService.class);
        context.startService(background);

        ct = context;

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("SCREEN","SCREEN ON");

            SharedPreferences preferences = ct.getSharedPreferences("perso", Context.MODE_PRIVATE);
            preferences.edit().putBoolean("SCREEN_ON",true).apply();
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("SCREEN", "SCREEN OFF");

            SharedPreferences preferences = ct.getSharedPreferences("perso", Context.MODE_PRIVATE);
            preferences.edit().putBoolean("SCREEN_OFF",true).apply();

            announceForAccessibilityCompat();
        }
    }

    private void announceForAccessibilityCompat() {

        // Prior to SDK 16, announcements could only be made through FOCUSED
        // events. Jelly Bean (SDK 16) added support for speaking text verbatim
        // using the ANNOUNCEMENT event type.


        final int eventType;
        if (Build.VERSION.SDK_INT < 16) {
            eventType = AccessibilityEvent.TYPE_VIEW_FOCUSED;
        } else {
            eventType = AccessibilityEventCompat.TYPE_ANNOUNCEMENT;
        }

        // Construct an accessibility event with the minimum recommended
        // attributes. An event without a class name or package may be dropped.
        final AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
        event.setEnabled(true);
        event.setEventType(TYPE_WINDOW_STATE_CHANGED);
        event.setClassName(getClass().getName());
        event.setPackageName(ct.getPackageName());

        // Sends the event directly through the accessibility manager. If your
        // application only targets SDK 14+, you should just call
        // getParent().requestSendAccessibilityEvent(this, event);
        AccessibilityManager mA11yManager = (AccessibilityManager) ct.getSystemService(ct.ACCESSIBILITY_SERVICE);
        if (mA11yManager != null) {
            mA11yManager.sendAccessibilityEvent(event);
            Log.d("SCREEN", "EVENT SEND");
        }
        else Log.d("SCREEN", "EVENT NOT SEND");
    }

}

