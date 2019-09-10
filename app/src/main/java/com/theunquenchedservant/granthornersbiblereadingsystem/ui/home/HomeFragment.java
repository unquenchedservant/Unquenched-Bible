package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView current_date = (TextView)root.findViewById(R.id.current_date);
        Date today = Calendar.getInstance().getTime();
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String str_today = formatter.format(today);
        current_date.setText(str_today);
        SharedPreferences prefRead = getActivity().getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        String check = prefRead.getString("dateClicked", "May 4");
        if(check.equals(str_today)){
            Button button = (Button)root.findViewById(R.id.material_button);
            button.setText("Done!");
            button.setEnabled(false);
        }
        setList(root, "List 1", R.array.list_1, R.id.list1_reading);
        setList(root, "List 2", R.array.list_2, R.id.list2_reading);
        setList(root, "List 3", R.array.list_3, R.id.list3_reading);
        setList(root, "List 4", R.array.list_4, R.id.list4_reading);
        setList(root, "List 5", R.array.list_5, R.id.list5_reading);
        int psCheck = getPsalmCheck();
        if(psCheck == 1){
            TextView list_reading = (TextView)root.findViewById(R.id.list6_reading);
            String pa1 = "Psalm " + Integer.toString(day) + ", " + Integer.toString(day + 30) + ", " + Integer.toString(day + 60) + ", " + Integer.toString(day + 90) + ", " + Integer.toString(day + 120);
            list_reading.setText(pa1);
        }else {
            setList(root, "List 6", R.array.list_6, R.id.list6_reading);
        }
        setList(root, "List 7", R.array.list_7, R.id.list7_reading);
        setList(root, "List 8", R.array.list_8, R.id.list8_reading);
        setList(root, "List 9", R.array.list_9, R.id.list9_reading);
        setList(root, "List 10", R.array.list_10, R.id.list10_reading);
        //TODO reset button
        return root;
    }
    public int getPsalmCheck(){
        SharedPreferences pref = getActivity().getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        return pref.getInt("psalmSwitch", 0);
    }
    public void setList(View view, String list_string, int listId, int readingId){
        SharedPreferences pref = getActivity().getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        Resources res = getResources();
        int list_num = pref.getInt(list_string, 0);
        String[] list = res.getStringArray(listId);
        String reading = list[list_num];
        TextView list_reading = (TextView)view.findViewById(readingId);
        list_reading.setText(reading);
    }
}