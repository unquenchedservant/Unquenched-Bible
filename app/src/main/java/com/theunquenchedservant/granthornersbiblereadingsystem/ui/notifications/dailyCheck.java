package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;

import java.util.Calendar;

public class dailyCheck extends BroadcastReceiver {
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        String check = MainActivity.prefReadString(context, "dateClicked");
        String str_today = MainActivity.getYesterdayDate(false);
        if(check.equals(str_today)){

        }else{
            MainActivity.prefEditInt(context, "curStreak", 0);
        }
    }
}
