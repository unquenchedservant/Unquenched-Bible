package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import androidx.core.app.NotificationCompat;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

import static com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.getContent;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID=0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        deliverNotification(context);
    }
    private void deliverNotification(Context context){
        String today = MainActivity.getCurrentDate(false);
        Intent finishIntent = new Intent(context, AlarmReceiver.class);
        finishIntent.putExtra("action","Done");
        String content = MainActivity.getContent(context);
        String smallContent = MainActivity.getSmallContent(context);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, finishIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(today)
                .setContentText(smallContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content));
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
