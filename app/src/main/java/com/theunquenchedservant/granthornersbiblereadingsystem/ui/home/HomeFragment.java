package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        SharedPreferences prefs = getActivity().getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView current_date = (TextView)root.findViewById(R.id.current_date);
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String str_today = formatter.format(today);
        current_date.setText(str_today);
        Resources res = getResources();
        int list1_num = prefs.getInt("List 1", 0);
        String[] list_1 = res.getStringArray(R.array.list_1);
        String reading = list_1[list1_num];
        TextView list1_reading = (TextView)root.findViewById(R.id.list1_reading);
        list1_reading.setText(reading);
        return root;
    }
}