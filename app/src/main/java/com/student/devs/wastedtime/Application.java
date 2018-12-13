package com.student.devs.wastedtime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Application
{
    ////////////////////ARGUMENTS/////////////////////////
    private int id;
    private String Appli;
    private int RealTime;
    private int EstimatedTime;
    private String heure;
    private String user;
    private DateFormat df = new SimpleDateFormat("HH:mm ; dd/MM/yyyy");



    ////////////////////Constructeurs/////////////////////////
    public Application()
    {
        this.id=0;
        this.Appli="";
        this.RealTime=0;
        this.EstimatedTime=0;
        this.heure= Calendar.getInstance().getTime().toString();
        this.user="";
    }

    public Application(String mAppli, String mUser) {
        this.id = 0;
        this.Appli = mAppli;
        this.RealTime = 0;
        this.EstimatedTime = 0;
        this.heure = Calendar.getInstance().getTime().toString();
        this.user = mUser;
    }

    public Application(String mAppli, String mUser, int mEstimatedTime, int mRealTime) {
        this.id = 0;
        this.Appli = mAppli;
        this.RealTime = mEstimatedTime;
        this.EstimatedTime = mRealTime;
        this.heure = df.format(Calendar.getInstance().getTime());
        this.user = mUser;
    }


    ////////////////////Méthodes/////////////////////////
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppli() {
        return Appli;
    }

    public void setAppli(String appli) {
        Appli = appli;
    }

    public int getRealTime() {
        return RealTime;
    }

    public void setRealTime(int realTime) {
        RealTime = realTime;
    }

    public int getEstimatedTime() {
        return EstimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        EstimatedTime = estimatedTime;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
