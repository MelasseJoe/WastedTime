package com.student.devs.wastedtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class BootCompletedReceiver extends BroadcastReceiver {

    Context ct;
    Intent it;

    @Override
    public void onReceive(Context context, Intent arg1) {
        Intent background = new Intent(context, UpdateService.class);
        context.startService(background);
    }


}