package com.student.devs.wastedtime;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class WindowChangeDetectingService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean isServiceActive = Utils.getSharedPref(this, R.string.sharedprefs_key1, false);
        if (isServiceActive && event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
            ActivityInfo activityInfo = getActivityInfo(componentName);
            boolean isActivity = activityInfo != null;
            if (isActivity) {
                String activityName = componentName.flattenToShortString();
                //Toast.makeText(this, activityName, Toast.LENGTH_LONG).show();

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("perso", Context.MODE_PRIVATE);
                PackageManager pm = getPackageManager();
                String app_name = preferences.getString("packageNameStart", "");

                try {
                    app_name = (String)pm.getApplicationLabel(pm.getApplicationInfo(preferences.getString("packageNameStart", ""), PackageManager.GET_META_DATA));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                if(!componentName.getPackageName().equals(preferences.getString("packageNameStart", ""))) {

                    Date myDate = new Date(preferences.getLong("timeStart", -1));

                    Date currentTime = Calendar.getInstance().getTime();

                    long time_diff = (currentTime.getTime() - myDate.getTime()) / 1000;
                    Toast.makeText(this, "Vous etes reste " + String.valueOf(time_diff) + " secondes sur " + app_name, Toast.LENGTH_LONG).show();

                    preferences.edit().putLong("timeStart", currentTime.getTime()).apply();
                    preferences.edit().putString("packageNameStart", componentName.getPackageName()).apply();
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        // Ignored
    }

    private ActivityInfo getActivityInfo(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
