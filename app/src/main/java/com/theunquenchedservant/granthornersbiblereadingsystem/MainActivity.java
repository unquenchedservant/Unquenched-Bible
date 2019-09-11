package com.theunquenchedservant.granthornersbiblereadingsystem;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications.AlarmReceiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID=0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;
    private Resources res = getResources();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        createNotificationChannel();
        Switch alarmToggle = findViewById(R.id.notificationToggle);
    }
    public static String getContent(Context context){
        String today = getCurrentDate(false);
        String content;
        String list1 = getListContent(context, "List 1", R.array.list_1);
        String list2 = getListContent(context, "List 2", R.array.list_2);
        String list3 = getListContent(context, "List 3", R.array.list_3);
        String list4 = getListContent(context, "List 4", R.array.list_4);
        String list5 = getListContent(context, "List 5", R.array.list_5);
        String list6 = getListContent(context, "List 6", R.array.list_6);
        String list7 = getListContent(context, "List 7", R.array.list_7);
        String list8 = getListContent(context, "List 8", R.array.list_8);
        String list9 = getListContent(context,"List 9", R.array.list_9);
        String list10 = getListContent(context, "List 10", R.array.list_10);
        content = list1 + "\n" + list2 + "\n" + list3 + "\n" + list4 + "\n" + list5 + "\n" + list6 + "\n" + list7 + "\n" + list8 + "\n" + list9 + "\n" + list10;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "daily")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(today)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(3, builder.build());
        return content;
    }
    public static String getListContent(Context context, String listString, int ArrayId){
        String[] list = Resources.getSystem().getStringArray(ArrayId);
        int number = prefReadInt(context, listString);
        return list[number];
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("daily", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void reset(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                prefEditInt(MainActivity.this, "List 1", 0);
                prefEditInt(MainActivity.this, "List 2", 0);
                prefEditInt(MainActivity.this, "List 3", 0);
                prefEditInt(MainActivity.this, "List 4", 0);
                prefEditInt(MainActivity.this, "List 5", 0);
                prefEditInt(MainActivity.this, "List 6", 0);
                prefEditInt(MainActivity.this,"List 7", 0);
                prefEditInt(MainActivity.this,"List 8", 0);
                prefEditInt(MainActivity.this,"List 9", 0);
                prefEditInt(MainActivity.this,"List 10", 0);
                prefEditString(MainActivity.this,"dateClicked", "None");
                NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_home);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setMessage(R.string.reset_confirm)
                .setTitle(R.string.reset_title);
        builder.create().show();
    }
    public void setChecked(View view){
        Switch psSwitch = view.findViewById(R.id.psalms_switch);
        int data;
        if(psSwitch.isChecked()){
            data = 1;
        }else{
            data = 0;
        }
        prefEditInt(this, "psalmSwitch", data);
    }
    public void
    public void markAll(View view) {
        String today = getCurrentDate(false);
        String check = prefReadString(this, "dateClicked");
        if(!check.equals(today)){
            Log.d("Today", today);
            Log.d("Check", check);
            prefEditString(this, "dateClicked", today);
            Button button = view.findViewById(R.id.material_button);
            button.setText(R.string.done);
            button.setEnabled(false);
            markList("List 1", R.array.list_1);
            markList("List 2", R.array.list_2);
            markList("List 3", R.array.list_3);
            markList("List 4", R.array.list_4);
            markList("List 5", R.array.list_5);
            int psCheck = prefReadInt(this, "psalmSwitch");
            if(psCheck == 0){
                markList("List 6", R.array.list_6);
            }
            markList("List 7", R.array.list_7);
            markList("List 8", R.array.list_8);
            markList("List 9", R.array.list_9);
            markList("List 10", R.array.list_10);
        }
    }
    public static String getCurrentDate(Boolean fullMonth){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter;
        if(fullMonth){
            formatter = new SimpleDateFormat("MMMM dd", Locale.US);
        }else {
            formatter = new SimpleDateFormat("MMM dd", Locale.US);
        }
        return formatter.format(today);
    }
    public void markList(String listString, int arrayId){
        int number = prefReadInt(this, listString);
        number++;
        Resources res = getResources();
        String[] list = res.getStringArray(arrayId);
        if(number == list.length){
            number = 0;
        }
        setList(listString, number);
    }
    public void setList(String listString, int number){
        prefEditInt(this, listString, number);
    }
    public static SharedPreferences getPrefRead(Context context){
        return context.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
    }
    public static SharedPreferences.Editor getPrefEdit(Context context){
        return context.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit();
    }
    public static String prefReadString(Context context, String stringName){
        SharedPreferences pref = getPrefRead(context);
        return pref.getString(stringName, "");
    }
    public static int prefReadInt(Context context, String intName){
        SharedPreferences pref = getPrefRead(context);
        return pref.getInt(intName, 0);
    }
    public static void prefEditString(Context context, String name, String str){
        SharedPreferences.Editor pref= getPrefEdit(context);
        pref.putString(name, str);
        pref.apply();
    }
    public static void prefEditInt(Context context, String name, int x){
        SharedPreferences.Editor pref = getPrefEdit(context);
        pref.putInt(name, x);
        pref.apply();
    }
    public static void deliverNotification(Context context){
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}

