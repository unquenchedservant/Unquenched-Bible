package com.theunquenchedservant.granthornersbiblereadingsystem.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        int psCheck = MainActivity.prefReadInt(getActivity(), "psalmSwitch");
        Switch psSwitch = root.findViewById(R.id.psalms_switch);
        if(psCheck == 1){
            psSwitch.setChecked(true);
        }else{
            psSwitch.setChecked(false);
        }
        Spinner list1Force = (Spinner) root.findViewById(R.id.list1Spinner);
        Spinner list2Force = (Spinner) root.findViewById(R.id.list2Spinner);
        Spinner list3Force = (Spinner) root.findViewById(R.id.list3Spinner);
        Spinner list4Force = (Spinner) root.findViewById(R.id.list4Spinner);
        Spinner list5Force = (Spinner) root.findViewById(R.id.list5Spinner);
        Spinner list6Force = (Spinner) root.findViewById(R.id.list6Spinner);
        Spinner list7Force = (Spinner) root.findViewById(R.id.list7Spinner);
        Spinner list8Force = (Spinner) root.findViewById(R.id.list8Spinner);
        Spinner list9Force = (Spinner) root.findViewById(R.id.list9Spinner);
        Spinner list10Force = (Spinner) root.findViewById(R.id.list10Spinner);
        listSetter(list1Force, root, getActivity(), R.array.list_1, "List 1");
        listSetter(list2Force, root, getActivity(), R.array.list_2, "List 2");
        listSetter(list3Force, root, getActivity(), R.array.list_3, "List 3");
        listSetter(list4Force, root, getActivity(), R.array.list_4, "List 4");
        listSetter(list5Force, root, getActivity(), R.array.list_5, "List 5");
        listSetter(list6Force, root, getActivity(), R.array.list_6, "List 6");
        listSetter(list7Force, root, getActivity(), R.array.list_7, "List 7");
        listSetter(list8Force, root, getActivity(), R.array.list_8, "List 8");
        listSetter(list9Force, root, getActivity(), R.array.list_9, "List 9");
        listSetter(list10Force, root, getActivity(), R.array.list_10, "List 10");
        return root;
    }
    public void listSetter(final Spinner spinner, View view, final Context context, int arrayId, final String listName){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(MainActivity.prefReadInt(context, listName));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                MainActivity.prefEditInt(context, listName, pos);
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent){

           }
        });
    }
}