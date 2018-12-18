package com.student.devs.wastedtime;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MyBDD_Global extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "WastedTimed_Global";

    private static final int DATABASE_VERSION = 1;

    public static String getTABLE_Application() {
        return TABLE_Application;
    }

    private static final String TABLE_Application = "WastedTimed_Global";

    private static final String ID = "ID";
    private static final String NAME = "user";
    private static final String RealTime = "RealTime";
    private static final String EstimatedTime = "EstimatedTime";
    private static final String Appli = "Appli";
    private static final String Hour = "heure";






    // Commande sql pour la création de la base de donnée
    private static final String DATABASE_CREATE_APPLICATIONS = "create table "
            + TABLE_Application + "(" +
            ID + " integer primary key autoincrement, " +
            NAME + " TEXT, " +
            RealTime + " INTEGER, " +
            EstimatedTime + " INTEGER, " +
            Appli + " TEXT, " +
            Hour + " TEXT);";

    public MyBDD_Global(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_APPLICATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MyBDD_Global.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Application);
        onCreate(db);
    }

    public void addAppli(Application application){

        List<Application> applicationList = getApplications();

        int id = -1;

        for(Application app : applicationList)
        {
            if(app.getAppli().equals(application.getAppli()))
            {
                id = app.getId();
            }
        }

        if(id == -1)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NAME, application.getUser());
            values.put(RealTime, application.getRealTime());
            values.put(EstimatedTime, application.getEstimatedTime());
            values.put(Appli, application.getAppli());
            values.put(Hour, application.getHeure());

            db.insert(TABLE_Application, null, values);
            db.close();
        }
        else {
            Application app = getApplication(id);

            deleteAppli(app);

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NAME, application.getUser());
            values.put(RealTime, application.getRealTime() + app.getRealTime());
            values.put(EstimatedTime, application.getEstimatedTime());
            values.put(Appli, application.getAppli());
            values.put(Hour, application.getHeure());

            db.insert(TABLE_Application, null, values);
            db.close();
        }
    }

    public void deleteAppli(Application application) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {application.getId()+""};
        db.delete(TABLE_Application, ID + " = ?", whereArgs);
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_Application);
        db.close();
    }

    public Application getApplication(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Application application = new Application();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_Application + " WHERE " + ID + "=" + id, null);
        if (c.moveToFirst()){
            application.setId(c.getInt(c.getColumnIndex(ID)));
            application.setAppli(c.getString(c.getColumnIndex(Appli)));
            application.setEstimatedTime(c.getInt(c.getColumnIndex(EstimatedTime)));
            application.setHeure(c.getString(c.getColumnIndex(Hour)));
            application.setRealTime(c.getInt(c.getColumnIndex(RealTime)));
            application.setUser(c.getString(c.getColumnIndex(NAME)));
        }
        return application;
    }

    public List<Application> getApplications() {
        List<Application> applicationList = new ArrayList<Application>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_Application, null);
        if (c.moveToFirst()) {
            do {
                Application application = new Application();
                application.setId(c.getInt(c.getColumnIndex(ID)));
                application.setAppli(c.getString(c.getColumnIndex(Appli)));
                application.setEstimatedTime(c.getInt(c.getColumnIndex(EstimatedTime)));
                application.setHeure(c.getString(c.getColumnIndex(Hour)));
                application.setRealTime(c.getInt(c.getColumnIndex(RealTime)));
                application.setUser(c.getString(c.getColumnIndex(NAME)));
                applicationList.add(application);
            } while (c.moveToNext());
        }
        db.close();
        return applicationList;
    }

    public List<Application> getTopNApplications(int n) {
        List<Application> applicationList = new ArrayList<Application>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_Application, null);
        if (c.moveToFirst()) {
            do {
                Application application = new Application();
                application.setId(c.getInt(c.getColumnIndex(ID)));
                application.setAppli(c.getString(c.getColumnIndex(Appli)));
                application.setEstimatedTime(c.getInt(c.getColumnIndex(EstimatedTime)));
                application.setHeure(c.getString(c.getColumnIndex(Hour)));
                application.setRealTime(c.getInt(c.getColumnIndex(RealTime)));
                application.setUser(c.getString(c.getColumnIndex(NAME)));
                applicationList.add(application);
            } while (c.moveToNext());
        }
        db.close();
        Comparator<Application> comparator = new Comparator<Application>() {
            @Override
            public int compare(Application left, Application right) {
                return right.getRealTime() - left.getRealTime();
            }
        };
        Collections.sort(applicationList, comparator);
        return applicationList.subList(0, n);
    }

    public int getApplicationCount() {
        String countQuery = "SELECT * FROM " + TABLE_Application;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }


}
