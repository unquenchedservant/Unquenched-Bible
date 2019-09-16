package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.Notification;
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
        String[] content = MainActivity.getContent(context);
        Intent tapIntent = new Intent(context, MainActivity.class);
        tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent tapPending = PendingIntent.getActivity(context, 0, tapIntent, 0);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        Intent detailsIntent = new Intent(context, doneReceiver.class);
        PendingIntent detailsPendingIntent = PendingIntent.getBroadcast(context, 0 , detailsIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(today)
                .setStyle(new NotificationCompat.InboxStyle()
                    .addLine(content[0])
                    .addLine(content[1])
                    .addLine(content[2])
                    .addLine(content[3])
                    .addLine(content[4]))
                .setContentText("Expand For The List...")
                .addAction(android.R.drawable.ic_menu_save, "Done", detailsPendingIntent)
                .setContentIntent(tapPending)
                .setAutoCancel(false);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
