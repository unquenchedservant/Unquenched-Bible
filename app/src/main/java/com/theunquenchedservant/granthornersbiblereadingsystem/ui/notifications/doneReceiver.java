package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

import static com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.getCurrentDate;

public class doneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String check = MainActivity.prefReadString(context, "dateClicked");
        String today = getCurrentDate(false);
        if(!check.equals(today)) {
            MainActivity.markListStatic("List 1", R.array.list_1, context);
            MainActivity.markListStatic("List 2", R.array.list_2, context);
            MainActivity.markListStatic("List 3", R.array.list_3, context);
            MainActivity.markListStatic("List 4", R.array.list_4, context);
            MainActivity.markListStatic("List 5", R.array.list_5, context);
            MainActivity.markListStatic("List 6", R.array.list_6, context);
            MainActivity.markListStatic("List 7", R.array.list_7, context);
            MainActivity.markListStatic("List 8", R.array.list_8, context);
            MainActivity.markListStatic("List 9", R.array.list_9, context);
            MainActivity.markListStatic("List 10", R.array.list_10, context);
            Toast.makeText(context, "Lists Marked!", Toast.LENGTH_LONG)
                    .show();
            MainActivity.prefEditString(context, "dateClicked", today);
        }else{
            Toast.makeText(context, "Already Done!", Toast.LENGTH_LONG).show();
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);

    }
}
