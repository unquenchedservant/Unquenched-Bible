package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

public class remindReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID=1;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String check = MainActivity.prefReadString(context, "dateClicked");
        String str_today = MainActivity.getCurrentDate(false);
        if(check.equals(str_today)){
            Log.d("Notification", "Remind not needed");
        }else{
            Log.d("Notification", "Remind sent");
            deliverNotification(context);
        }
    }

    private void deliverNotification(Context context){
        String rem = context.getResources().getString(R.string.remTitle);
        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent tapPending = PendingIntent.getActivity(context, 0, tapIntent, 0);
        Intent detailsIntent = new Intent(context, doneReceiver.class);
        PendingIntent detailsPendingIntent = PendingIntent.getBroadcast(context, 0 , detailsIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(rem)
                .setContentText("Don't Forget To Do The Reading!")
                .addAction(android.R.drawable.ic_menu_save, "Done", detailsPendingIntent)
                .setContentIntent(tapPending)
                .setAutoCancel(false);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
