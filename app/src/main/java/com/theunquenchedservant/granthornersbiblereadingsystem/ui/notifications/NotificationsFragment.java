package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

//TODO Add time option
//TODO Add save button (only update alarmmanager when saved)
public class NotificationsFragment extends Fragment {
    private static final int NOTIFICATION_ID=0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        Switch notificationToggle = root.findViewById(R.id.notificationToggle);
        if(MainActivity.prefReadInt(getActivity(), "notifications") == 1){
            notificationToggle.setChecked(true);
        }else{
            notificationToggle.setChecked(false);
        }
        notificationToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        String toastMessage;
                        Intent notifyIntent = new Intent(getActivity(), AlarmReceiver.class);
                        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(getActivity(), NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if(isChecked){
                            MainActivity.prefEditInt(getActivity(), "notifications", 1);
                            MainActivity.createAlarm(getActivity(), notifyPendingIntent);
                            toastMessage = "Notifications On";
                        } else {
                            MainActivity.prefEditInt(getActivity(), "notifications", 0);
                            MainActivity.cancelAlarm(getActivity(), notifyPendingIntent);
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