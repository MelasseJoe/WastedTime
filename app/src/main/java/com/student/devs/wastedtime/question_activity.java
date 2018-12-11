package com.student.devs.wastedtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class question_activity extends Activity {

    String app_name;
    Drawable app_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);

        Intent i = getIntent();
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

        //Création de l'AlertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(question_activity.this);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);

        adb.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void edit_time(View v)
    {
        LayoutInflater factory = LayoutInflater.from(question_activity.this);
        final View alertDialogView = factory.inflate(R.layout.time_picker, null);

        TimePicker timePicker = alertDialogView.findViewById(R.id.timePicker);
        int time = timePicker.getHour() * 60 + timePicker.getMinute();

        Toast.makeText(this, "Vous pensez être rester " + time + " minutes sur " + app_name, Toast.LENGTH_LONG).show();

    }

}
