package com.student.devs.wastedtime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Send extends AsyncTask<String,String,String> {
    private static final String DB_URL = "jdbc:mysql://162.38.134.172/wastedtime";
    private static final String USER = "wastedtime";
    private static final String PASS = "Proj92!emi";
    private static MyBDD myBDD;

    @Override
    protected void onPreExecute() {}

    public static void setMyBDD(MyBDD myBDD_)
    {
        myBDD = myBDD_;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            if (conn == null) {
                //TODO//On a pas de connection internet ou l'envoi n'a pas marché
                Log.d("ERROR", "Pas de connection internet");
            } else {
                List<Application> tab_app = new ArrayList<>();
                tab_app.addAll(myBDD.getApplications());
                String query = "INSERT INTO Application(NAME, EstimatedTime, RealTime, Appli, Hour) VALUES";  //text
                for(int i = 0; i < tab_app.size() - 1; i++)
                {
                    query += "('" + tab_app.get(i).getUser() + "' , '" + tab_app.get(i).getRealTime() + "' , '" + tab_app.get(i).getEstimatedTime() + "' , '" + tab_app.get(i).getAppli() + "' , '"  + tab_app.get(i).getHeure() + "'),";  //text
                }
                query += "('" + tab_app.get(tab_app.size() - 1).getUser() + "' , '" + tab_app.get(tab_app.size() - 1).getRealTime() + "' , '" + tab_app.get(tab_app.size() - 1).getEstimatedTime() + "' , '" + tab_app.get(tab_app.size() - 1).getAppli() + "' , '"  + tab_app.get(tab_app.size() - 1).getHeure() + "');";
                myBDD.deleteAll();
                Log.d("ERROR", query);
                Statement stmt = conn.createStatement();
                stmt.execute(query);

            }
            assert conn != null;
            conn.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String msg)
    {
    }
}
