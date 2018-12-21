package com.student.devs.wastedtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class question_activity_global extends Activity {

    String app_name;
    Drawable app_icon;
    TimePicker timePicker;
    AlertDialog.Builder adb;
    AlertDialog alertDialog;
    int count;
    List<Application> listApp;
    MyBDD_Global myBDD_global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);

        Intent i = getIntent();
        count = 4;

        if(i.getBooleanExtra("Top3",false))
        {
            count = 0;
            myBDD_global = new MyBDD_Global(this);
            listApp = myBDD_global.getTopNApplications(3);

            app_name = listApp.get(count).getAppli();

            TextView textView = findViewById(R.id.name_app);
            textView.setText(app_name);
        }
        else {
            String package_name = i.getStringExtra("package");
            PackageManager pm = getPackageManager();

            try {
                app_name = (String) pm.getApplicationLabel(pm.getApplicationInfo(package_name, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                app_name = "No Activity Name";
                e.printStackTrace();
            }

            try {
                app_icon = getPackageManager().getApplicationIcon(package_name);
            } catch (PackageManager.NameNotFoundException e) {
                app_icon = getDrawable(R.drawable.no_image);
                e.printStackTrace();
            }

            ImageView imageView = findViewById(R.id.icon_app);
            TextView textView = findViewById(R.id.name_app);

            imageView.setBackground(app_icon);
            textView.setText(app_name);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void wastedTime(View v)
    {
        LayoutInflater factory = LayoutInflater.from(question_activity_global.this);
        final View alertDialogView = factory.inflate(R.layout.time_picker, null);
        timePicker = alertDialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setHour(0);
        timePicker.setMinute(0);
        //Création de l'AlertDialog
        adb = new AlertDialog.Builder(question_activity_global.this);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        if(count < 3) {
                            dialog.cancel();
                            int hours = timePicker.getHour();
                            int minutes = timePicker.getMinute();
                            Toast.makeText(getApplicationContext(), "Vous pensez être resté " + hours + " heures " + minutes + " minutes sur " + myBDD_global.getApplication(count).getAppli(), Toast.LENGTH_LONG).show();

                            //Envoi de l'humeur dans la base de donnée du serveur
                            Send objSend = new Send();
                            objSend.setMyBDD_Global(myBDD_global);
                            objSend.execute(String.valueOf(count));
                        }
                        else {
                            dialog.cancel();
                            int hours = timePicker.getHour();
                            int minutes = timePicker.getMinute();
                            Toast.makeText(getApplicationContext(), "Vous pensez être resté " + hours + " heures " + minutes + " minutes sur " + app_name, Toast.LENGTH_LONG).show();

                            //Creation de la base de donnée
                            MyBDD database = new MyBDD(getApplicationContext());

                            //Creation d'une humeur à partir de l'id de l'utilisateur et de son humeur
                            Application appli = new Application(app_name, readData("id_user").substring(7), (hours * 60 + minutes), (int) getIntent().getLongExtra("timeDiff", -1) / 60);

                            //Ajout de l'humeur dans la base de donnée
                            database.addAppli(appli);

                            //Envoi de l'humeur dans la base de donnée du serveur
                            Send objSend = new Send();
                            objSend.setMyBDD(database);
                            objSend.execute("a");
                            finish();
                        }
                    }
                });
        //adb.show();

        alertDialog = adb.create();
        alertDialog.show();
        //alertDialog.getWindow().setLayout(950, 900); //Controlling width and height.
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
