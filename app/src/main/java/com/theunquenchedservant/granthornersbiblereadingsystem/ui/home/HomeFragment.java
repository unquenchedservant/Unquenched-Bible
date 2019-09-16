package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.unquenchedEmph));

                textView.setAllCaps(true);
            }
        });
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String str_today = MainActivity.getCurrentDate(false);
        String check = MainActivity.prefReadString(getActivity(), "dateClicked");
        if(check.equals(str_today)){
            Button button = root.findViewById(R.id.material_button);
            button.setText(R.string.done);
            button.setEnabled(false);
        }
        setList(root, "List 1", R.array.list_1, R.id.list1_reading);
        setList(root, "List 2", R.array.list_2, R.id.list2_reading);
        setList(root, "List 3", R.array.list_3, R.id.list3_reading);
        setList(root, "List 4", R.array.list_4, R.id.list4_reading);
        setList(root, "List 5", R.array.list_5, R.id.list5_reading);
        int psCheck = MainActivity.prefReadInt(getActivity(),"psalmSwitch");
        if(psCheck == 1){
            TextView list_reading = (root.findViewById(R.id.list6_reading));
            String pa1 = "Psalm " + day + ", " + Integer.toString(day + 30) + ", " + Integer.toString(day + 60) + ", " + Integer.toString(day + 90) + ", " + Integer.toString(day + 120);
            list_reading.setText(pa1);
        }else {
            setList(root, "List 6", R.array.list_6, R.id.list6_reading);
        }
        setList(root, "List 7", R.array.list_7, R.id.list7_reading);
        setList(root, "List 8", R.array.list_8, R.id.list8_reading);
        setList(root, "List 9", R.array.list_9, R.id.list9_reading);
        setList(root, "List 10", R.array.list_10, R.id.list10_reading);
        return root;
    }
    private void setList(View view, String list_string, int listId, int readingId){
        Resources res = getResources();
        int listNum = MainActivity.prefReadInt(getActivity(),list_string);
        String[] list = res.getStringArray(listId);
        String reading = list[listNum];
        TextView listReading = view.findViewById(readingId);
        listReading.setText(reading);
    }
}