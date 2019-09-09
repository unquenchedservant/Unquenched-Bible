package com.theunquenchedservant.granthornersbiblereadingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
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
    }
    public void markAll(View view) {
        markList("List 1");
        markList("List 2");
    }
    public void markList(String listString){
        int number = getListNumber(listString);
        number++;
        setList(listString, number);
    }
    public int getListNumber(String listString){
        SharedPreferences prefs = this.getSharedPreferences(
                "com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        int list_num = prefs.getInt(listString, 0);
        return list_num;
    }
    public void setList(String listString, int number){
        SharedPreferences.Editor prefs = this.getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE).edit();
        prefs.putInt(listString, number);
        prefs.apply();
    }
}
