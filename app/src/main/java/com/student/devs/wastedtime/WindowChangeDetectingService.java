package com.student.devs.wastedtime;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import java.util.Calendar;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class WindowChangeDetectingService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        int threshold_mediun = 10;
        int threshold_min = 3 * 60;

        boolean isServiceActive = Utils.getSharedPref(this, R.string.sharedprefs_key1, false);
        if (isServiceActive && event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
            ActivityInfo activityInfo = getActivityInfo(componentName);

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("perso", Context.MODE_PRIVATE);
            PackageManager pm = getPackageManager();

            String app_name = preferences.getString("packageNameStart", "");
            String packageNamePrev = preferences.getString("packageNamePrev","");
            String packageNameStart = preferences.getString("packageNameStart","");
            String packageNamePresent = componentName.getPackageName();
            long timeStart = preferences.getLong("timeStart", -1);
            long timePrev = preferences.getLong("timePrev", -1);
            long currentTime = Calendar.getInstance().getTime().getTime();

            try {
                app_name = (String)pm.getApplicationLabel(pm.getApplicationInfo(preferences.getString("packageNameStart", ""), PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            boolean isActivity = activityInfo != null;
            if (isActivity) {
                //si l'activity que l'on vient d'ouvrir n'appartient pas à la même appli que l'appli précédente
                if(!packageNamePresent.equals(packageNameStart)) {

                    //si le téléphone vient d'être déverouillé
                    if (PhoneUnlockedReceiver.unLocked){

                        PhoneUnlockedReceiver.unLocked = false;
                        preferences.edit().putLong("timeStart", currentTime).apply(); //permet d'éviter de compter le temps passé en Locked
                        /*
                            Avec cette méthode on ne compte plus le temps passé avec le téléphone verouillé comme du temps passé sur une appli
                            cependant quand l'utilisateur revient sur son téléphone, le premier changement d'activité ne sera pas comptabiliser
                            (ex: il unLock son téléphone et est directement sur YouTube,au bout de 30 min il va sur Facebook, le temps passé
                            sur Youtube ne sera pas comptabiliser car c'est l'activité qui a été ouverte juste aprés le "unLock")

                            Le problème se posera pas souvent mais parfois on perdra une donnée
                         */
                    }

                    long timeDiff = (currentTime - timeStart) ; /* "/1000" a remettre version finale*/

                    if(packageNamePrev.equals(packageNamePresent) && timeDiff < threshold_mediun)
                    {
                        preferences.edit().putLong("timePrev", timePrev + timeDiff).apply();
                        preferences.edit().putLong("timeStart", timePrev).apply();
                        preferences.edit().putString("packageNameStart", packageNamePrev).apply();

                    }
                    else if(timeDiff < threshold_mediun)
                    {
                        preferences.edit().putLong("timePrev", timePrev + timeDiff).apply();
                        preferences.edit().putLong("timeStart", currentTime).apply();
                        preferences.edit().putString("packageNameStart", packageNamePresent).apply();
                        // si ce n'est pas la première fois que l'utilisateur utilise WastedTime...
                        if (timeStart != -1){
                            Toast.makeText(this, "Vous êtes resté " + String.valueOf(timeDiff) + " secondes sur " + app_name, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        preferences.edit().putLong("timePrev", timeStart).apply();
                        preferences.edit().putString("packageNamePrev", packageNameStart).apply();
                        preferences.edit().putLong("timeStart", currentTime).apply();
                        preferences.edit().putString("packageNameStart", packageNamePresent).apply();
                        // si ce n'est pas la première fois que l'utilisateur utilise WastedTime...
                        if (timeStart != -1){
                            Toast.makeText(this, "Vous êtes resté " + String.valueOf(timeDiff) + " secondes sur " + app_name, Toast.LENGTH_LONG).show();
                        }
                    }
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


