package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;


//TODO Add time option
//TODO Add save button (only update alarmmanager when saved)
public class NotificationsFragment extends Fragment {
    EditText eText;
    EditText remindText;
    TimePickerDialog picker;
    int hour;
    int minute;
    int rHour;
    int rMinute;
    private static final int NOTIFICATION_ID=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final Switch notificationToggle = root.findViewById(R.id.notificationToggle);
        int notifChecked = MainActivity.prefReadInt(getActivity(), "notifications");
        Intent notifyIntent = new Intent(getActivity(), AlarmReceiver.class);
        Intent remindIntent = new Intent(getActivity(), remindReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent remindPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(notifChecked == 1){
            notificationToggle.setChecked(true);
            MainActivity.createAlarm(getActivity(), notifyPendingIntent);
            MainActivity.createRemindAlarm(getActivity(), remindPendingIntent);
        }else{
            notificationToggle.setChecked(false);
            MainActivity.cancelAlarm(getActivity(), notifyPendingIntent);
            MainActivity.cancelAlarm(getActivity(), remindPendingIntent);
        }
        hour = MainActivity.prefReadInt(getActivity(), "dailyHour");
        minute = MainActivity.prefReadInt(getActivity(), "dailyMinute");
        eText = (EditText) root.findViewById(R.id.editText1);
        eText.setText(String.format("%02d:%02d", hour, minute));
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                if(hour == 0) {
                    hour = cldr.get(Calendar.HOUR_OF_DAY);
                }
                if(minute == 0 && hour == 0) {
                    minute = cldr.get(Calendar.MINUTE);
                }
                // time picker dialog
                picker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                eText.setText(String.format("%02d:%02d", sHour, sMinute));
                                if(notificationToggle.isChecked()) {
                                    Intent notifyIntent = new Intent(getActivity(), AlarmReceiver.class);
                                    PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    MainActivity.createAlarm(getActivity(), notifyPendingIntent);
                                }
                                MainActivity.prefEditInt(getActivity(), "dailyHour", sHour);
                                MainActivity.prefEditInt(getActivity(), "dailyMinute", sMinute);
                            }
                        }, hour, minute, false);
                picker.show();
            }
        });
        rHour = MainActivity.prefReadInt(getActivity(), "remindHour");
        rMinute = MainActivity.prefReadInt(getActivity(), "remindMinute");
        remindText = (EditText) root.findViewById(R.id.remind_time);
        remindText.setText(String.format("%02d:%02d", rHour, rMinute));
        remindText.setInputType(InputType.TYPE_NULL);
        remindText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar cldr = Calendar.getInstance();
                if(rHour == 0){
                    hour = cldr.get(Calendar.HOUR_OF_DAY);
                }
                if(minute == 0 && hour == 0){
                    minute = cldr.get(Calendar.MINUTE);
                }
                picker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
                            remindText.setText(String.format("%02d:%02d", sHour, sMinute));
                            if(notificationToggle.isChecked()){
                                Intent notifyIntent = new Intent(getActivity(), remindReceiver.class);
                                PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                MainActivity.createRemindAlarm(getActivity(), notifyPendingIntent);
                            }
                            MainActivity.prefEditInt(getActivity(), "remindHour", sHour);
                            MainActivity.prefEditInt(getActivity(), "remindMinute", sMinute);
                        }
                    }, rHour, rMinute, false);
                picker.show();
            }
        });
        notificationToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        String toastMessage;
                        Intent notifyIntent = new Intent(getActivity(), AlarmReceiver.class);
                        Intent remindIntent = new Intent(getActivity(), remindReceiver.class);
                        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntent remindPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if(isChecked){
                            MainActivity.prefEditInt(getActivity(), "notifications", 1);
                            MainActivity.createAlarm(getActivity(), notifyPendingIntent);
                            MainActivity.createRemindAlarm(getActivity(), remindPendingIntent);
                            toastMessage = "Notifications On";
                        } else {
                            MainActivity.prefEditInt(getActivity(), "notifications", 0);
                            MainActivity.cancelAlarm(getActivity(), notifyPendingIntent);
                            MainActivity.cancelAlarm(getActivity(), remindPendingIntent);
                            toastMessage = "Notifications Off";
                        }

                        //Show a toast to say the alarm is turned on or off.
                        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        );
        return root;
    }
}