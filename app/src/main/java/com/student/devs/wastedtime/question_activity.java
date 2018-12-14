package com.student.devs.wastedtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class question_activity extends Activity {

    String app_name;
    Drawable app_icon;
    TimePicker timePicker;
    AlertDialog.Builder adb;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);

        i = getIntent();
        String package_name = i.getStringExtra("package");
        PackageManager pm = getPackageManager();

        try {
            app_name = (String)pm.getApplicationLabel(pm.getApplicationInfo(package_name, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            app_name = "No Activity Name";
            e.printStackTrace();
        }

        try
        {
            app_icon = getPackageManager().getApplicationIcon(package_name);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            app_icon = getDrawable(R.drawable.no_image);
            e.printStackTrace();
        }

        ImageView imageView = findViewById(R.id.icon_app);
        TextView textView = findViewById(R.id.name_app);

        imageView.setBackground(app_icon);
        textView.setText(app_name);

    }

    public void wastedTime(View v)
    {
        LayoutInflater factory = LayoutInflater.from(question_activity.this);
        final View alertDialogView = factory.inflate(R.layout.time_picker, null);
        timePicker = alertDialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        //Création de l'AlertDialog
        adb = new AlertDialog.Builder(question_activity.this);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        int hours = timePicker.getHour();
                        int minutes = timePicker.getMinute();
                        Toast.makeText(getApplicationContext(), "Vous pensez être resté " + hours +" heures " + minutes + " minutes sur " + app_name, Toast.LENGTH_LONG).show();

                        //Creation de la base de donnée
                        MyBDD database = new MyBDD(getApplicationContext());

                        //Creation d'une humeur à partir de l'id de l'utilisateur et de son humeur
                        Application appli = new Application(app_name, readData("id_user"), (hours*60*60 + minutes*60  ), (int) i.getLongExtra("timeDiff",-1));

                        //Ajout de l'humeur dans la base de donnée
                        database.addAppli(appli);

                        //Envoi de l'humeur dans la base de donnée du serveur
                        Send objSend = new Send();
                        objSend.setMyBDD(database);
                        objSend.execute("");


                        finish();
                    }
                });
        adb.show();
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true /*append*/));
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
        String textFromFile = "error";
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
                    textFromFile += "\n";
                }
                reader.close();
            } catch (Exception e) {
                Log.e("ReadWriteFile", "Unable to read the " + file + ".txt file.");
                return textFromFile;
            }
        }
        return textFromFile;
    }

}
