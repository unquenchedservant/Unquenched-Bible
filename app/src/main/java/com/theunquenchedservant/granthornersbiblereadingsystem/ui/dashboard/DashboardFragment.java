package com.theunquenchedservant.granthornersbiblereadingsystem.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theunquenchedservant.granthornersbiblereadingsystem.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        SharedPreferences pref = getActivity().getSharedPreferences("com.theunquenchedservant.granthornersbiblereadingsystem", Context.MODE_PRIVATE);
        Switch psSwitch = (Switch)root.findViewById(R.id.psalms_switch);
        int psCheck = pref.getInt("psalmSwitch", 0);
        if(psCheck == 1){
            psSwitch.setChecked(true);
        }else{
            psSwitch.setChecked(false);
        }
        return root;
    }
}