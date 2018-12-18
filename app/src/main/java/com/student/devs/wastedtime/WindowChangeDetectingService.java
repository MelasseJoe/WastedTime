package com.student.devs.wastedtime;

import android.accessibilityservice.AccessibilityService;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class WindowChangeDetectingService extends AccessibilityService {

    int threshold_mediun = 60;
    int threshold_min = 10*60;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        boolean isServiceActive = Utils.getSharedPref(this, R.string.sharedprefs_key1, false);
        if (isServiceActive && event.getEventType() == TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
            ActivityInfo activityInfo = getActivityInfo(componentName);

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("perso", Context.MODE_PRIVATE);
            PackageManager pm = getPackageManager();

            String app_name;
            String packageNamePrev = preferences.getString("packageNamePrev","");
            String packageNameStart = preferences.getString("packageNameStart","");
            String packageNamePresent = componentName.getPackageName();
            long timeStart = preferences.getLong("timeStart", -1);
            long timePrev = preferences.getLong("timePrev", -1);
            long currentTime = Calendar.getInstance().getTime().getTime();
            MyBDD_Global myBDD_global = new MyBDD_Global(this);

            try {
                app_name = (String)pm.getApplicationLabel(pm.getApplicationInfo(preferences.getString("packageNameStart", ""), PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                app_name = preferences.getString("packageNameStart", "");
                e.printStackTrace();
            }

            Log.d("SCREEN", app_name);

            boolean isActivity = activityInfo != null;
            if (isActivity) {

                //si le téléphone vient d'être déverouillé
                if (preferences.getBoolean("SCREEN_ON",false) & preferences.getBoolean("SCREEN_OFF",false)){

                    preferences.edit().putBoolean("SCREEN_ON",false).apply();
                    preferences.edit().putBoolean("SCREEN_OFF",false).apply();

                    preferences.edit().putLong("timePrev", -1).apply();
                    preferences.edit().putString("packageNamePrev", "").apply();
                    preferences.edit().putLong("timeStart", currentTime).apply();
                    preferences.edit().putString("packageNameStart", packageNamePresent).apply();

                    Log.d("Debug", "Phone unLocked");
                }

                //si le téléphone vient d'être déverouillé
                if (preferences.getBoolean("SCREEN_OFF",false)){

                    preferences.edit().putBoolean("HaveBeenLocked",false).apply();

                    long timeDiff = (currentTime - timeStart) / 1000 ;

                    if(timeDiff > threshold_min) {
                        showNotification(this, "Notification", "Combien de temps pensez vous avoir passer sur " + app_name + " ?", 1);
                    }

                    Log.d("Debug", "Phone unLocked");
                }

                //si l'activity que l'on vient d'ouvrir n'appartient pas à la même appli que l'appli précédente
                if(!packageNamePresent.equals(packageNameStart) & (preferences.getBoolean("SCREEN_OFF",false) == preferences.getBoolean("SCREEN_OFF",false))){

                    long timeDiff = (currentTime - timeStart) / 1000 ;

                    if(packageNamePrev.equals(packageNamePresent) && timeDiff < threshold_mediun)   //L'utilisateur revient sur la meme appli en moi de "threshold_medium" secondes
                    {
                        preferences.edit().putLong("timePrev", timePrev + timeDiff * 1000).apply();
                        preferences.edit().putLong("timeStart", timePrev + timeDiff * 1000).apply();
                        preferences.edit().putString("packageNameStart", packageNamePrev).apply();

                        Log.d("Debug", "if");
                        Log.d("Debug", "packageNamePresent   " + preferences.getString("packageNamePrev",""));
                        Log.d("Debug", "packageNameStart   " + preferences.getString("packageNameStart",""));
                    }
                    else if(timeDiff < threshold_mediun)    //L'utilisateur passe moins de "threshold_medium" secondes sur un appli donc on ne change pas l'applis "prev"
                    {
                        preferences.edit().putLong("timePrev", timePrev + timeDiff * 1000).apply();
                        preferences.edit().putLong("timeStart", currentTime).apply();
                        preferences.edit().putString("packageNameStart", packageNamePresent).apply();

                        Log.d("Debug", "else if");
                        Log.d("Debug", "packageNameStart   " + preferences.getString("packageNameStart",""));
                        Log.d("Debug", "packageNamePrev   " + preferences.getString("packageNamePrev",""));

                        Application appli = new Application(app_name, readData("id_user").substring(7), 0, (int)timeDiff);

                        myBDD_global.addAppli(appli);
                    }
                    else {
                        preferences.edit().putLong("timePrev", timeStart).apply();
                        preferences.edit().putString("packageNamePrev", packageNameStart).apply();
                        preferences.edit().putLong("timeStart", currentTime).apply();
                        preferences.edit().putString("packageNameStart", packageNamePresent).apply();

                        Log.d("Debug", "else");

                        // si ce n'est pas la première fois que l'utilisateur utilise WastedTime...
                        if (timeStart != -1){
                            Toast.makeText(this, "Vous êtes resté " + String.valueOf(timeDiff) + " secondes sur " + app_name, Toast.LENGTH_LONG).show();

                            Application appli = new Application(app_name, readData("id_user").substring(7), 0, (int)timeDiff);

                            myBDD_global.addAppli(appli);

                            if(timeDiff > threshold_min)
                            {
                                scheduleNotification("Combien de temps pensez vous avoir passer sur " + app_name + " ?", (threshold_mediun-1)*1000, packageNameStart, timeDiff);
                                //showNotification(getApplicationContext(),"Notification","Combien de temps pensez vous avoir passer sur " + app_name + " ?",1,new Intent(), packageNameStart);
                            }
                        }
                    }
                }
            }
        }
    }

    public void showNotification(Context context, String title, String body, int notificationId) {
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

        Intent i = new Intent(context, question_activity.class);
        i.putExtra("package", i.getStringExtra("package"));
        i.putExtra("timeDiff", i.getLongExtra("timeDiff",-1));

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    @Override
    public void onInterrupt() {
        // Ignored
        Log.d("Debug", "interrupt");
    }

    private ActivityInfo getActivityInfo(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
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

        Intent i = new Intent(this, question_activity.class);
        i.putExtra("package", package_name);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void scheduleNotification(String corps, int delay, String package_name, long timeDiff) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra("corps", corps);
        notificationIntent.putExtra("package", package_name);
        notificationIntent.putExtra("timeDiff", timeDiff);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }


    public String readData(String file)
    {
        String textFromFile = "";
        // Gets the file from the primary external storage space of the
        // current application.
        File testFile = new File(this.getExternalFilesDir(null), file + ".txt");
        if (testFile != null) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(testFile));
                String line;

                while ((line = reader.readLine()) != null) {
                    textFromFile += line.toString();
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the " + file + ".txt file.");
                return "error";
            }
        }
        return textFromFile;
    }
}