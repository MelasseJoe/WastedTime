package com.student.devs.wastedtime;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
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
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private EditText mEditPackage;
    private EditText mEditClass;
    private Switch mSwitch;
    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.RECEIVE_BOOT_COMPLETED,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.ACCESS_WIFI_STATE
                },
                1);

        //permet de détecter quand le téléphone est Locked ou unLocked
        startService(new Intent(MainActivity.this, UpdateService.class));


        synchroIdUser();

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

    public void synchroIdUser()
    {
        String id = readData("id_user");

        if(id.equals("error"))
        {
            Calendar calendar = Calendar.getInstance();
            id_user = String.valueOf(calendar.getTimeInMillis());

            writeData("id_user", id_user);
        }
        else
        {
            id_user = id;
        }
    }

    public void writeData(String file,String data)
    {
        try {
            // Creates a file in the primary external storage space of the
            // current application.
            // If the file does not exists, it is created.
            File testFile = new File(this.getExternalFilesDir(null), file + ".txt");
            if (!testFile.exists())
                testFile.createNewFile();

            // Adds a line to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, false /*append*/));
            writer.write(data);
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this,
                    new String[]{testFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
            Log.e("ReadWriteFile", "Unable to write to the " + file + ".txt file.");
        }
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
