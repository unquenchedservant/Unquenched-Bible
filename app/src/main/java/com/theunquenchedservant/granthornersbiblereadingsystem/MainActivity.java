package com.theunquenchedservant.granthornersbiblereadingsystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.dashboard.DashboardFragment;
import com.theunquenchedservant.granthornersbiblereadingsystem.ui.home.HomeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

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
        createNotificationChannel();
        notificationMaker();
    }
    private void notificationMaker(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String str_today = formatter.format(today);
        String content = "";
        String list1 = getListContent("List 1", R.array.list_1);
        String list2 = getListContent("List 2", R.array.list_2);
        String list3 = getListContent("List 3", R.array.list_3);
        String list4 = getListContent("List 4", R.array.list_4);
        String list5 = getListContent("List 5", R.array.list_5);
        String list6 = getListContent("List 6", R.array.list_6);
        String list7 = getListContent("List 7", R.array.list_7);
        String list8 = getListContent("List 8", R.array.list_8);
        String list9 = getListContent("List 9", R.array.list_9);
        String list10 = getListContent("List 10", R.array.list_10);
        content = list1 + "\n" + list2 + "\n" + list3 + "\n" + list4 + "\n" + list5 + "\n" + list6 + "\n" + list7 + "\n" + list8 + "\n" + list9 + "\n" + list10;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "daily")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(str_today)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(3, builder.build());
    }
    private String getListContent(String listString, int ArrayId){
        Resources res = getResources();
        String[] list = res.getStringArray(ArrayId);
        int number = getListNumber(listString);
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
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void reset(View view){
        SharedPreferences.Editor prefs = this.getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit();
        prefs.putInt("List 1", 0);
        prefs.putInt("List 2", 0);
        prefs.putInt("List 3", 0);
        prefs.putInt("List 4", 0);
        prefs.putInt("List 5", 0);
        prefs.putInt("List 6", 0);
        prefs.putInt("List 7", 0);
        prefs.putInt("List 8", 0);
        prefs.putInt("List 9", 0);
        prefs.putInt("List 10", 0);
        prefs.apply();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_home);
    }
    public void setChecked(View view){
        Switch psSwitch = (Switch)view.findViewById(R.id.psalms_switch);
        int data;
        if(psSwitch.isChecked()){
            data = 1;
        }else{
            data = 0;
        }
        SharedPreferences.Editor prefEdit = this.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit();
        prefEdit.putInt("psalmSwitch",data);
        prefEdit.apply();
    }
    public void markAll(View view) {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String str_today = formatter.format(today);
        SharedPreferences.Editor prefEdit = this.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit();
        SharedPreferences prefRead = this.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        String check = prefRead.getString("dateClicked", "May 4");
        if(!check.equals(str_today)){
            Log.d("Today", str_today);
            Log.d("Check", check);
            prefEdit.putString("dateClicked", str_today);
            prefEdit.apply();
            Button button = (Button)view.findViewById(R.id.material_button);
            button.setText("Done!");
            button.setEnabled(false);
            markList("List 1", R.array.list_1);
            markList("List 2", R.array.list_2);
            markList("List 3", R.array.list_3);
            markList("List 4", R.array.list_4);
            markList("List 5", R.array.list_5);
            int psCheck = prefRead.getInt("psalmSwitch", 0);
            if(psCheck == 0){
                markList("List 6", R.array.list_6);
            }
            markList("List 7", R.array.list_7);
            markList("List 8", R.array.list_8);
            markList("List 9", R.array.list_9);
            markList("List 10", R.array.list_10);
        }
    }
    public void markList(String listString, int arrayId){
        int number = getListNumber(listString);
        number++;
        Resources res = getResources();
        String[] list = res.getStringArray(arrayId);
        if(number == list.length){
            number = 0;
        }
        setList(listString, number);
    }
    public int getListNumber(String listString){
        SharedPreferences prefs = this.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        return prefs.getInt(listString, 0);
    }
    public void setList(String listString, int number){
        SharedPreferences.Editor prefs = this.getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit();
        prefs.putInt(listString, number);
        prefs.apply();
    }
}
