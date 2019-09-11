package com.theunquenchedservant.granthornersbiblereadingsystem.ui.notifications;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity;
import com.theunquenchedservant.granthornersbiblereadingsystem.R;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.deliverNotification;

public class NotificationsFragment extends Fragment {
    private AlarmManager alarmManager;
    private NotificationsViewModel notificationsViewModel;
    private NotificationManager mNotificationManager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Switch notificationToggle = root.findViewById(R.id.notificationToggle);
        notificationToggle.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        String toastMessage;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, 8);
                        calendar.set(Calendar.MINUTE, 0);

                        if(isChecked){
                            //Set the toast message for the "on" case.
                            alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                            toastMessage = "Notifications On";
                        } else {
                            //Set the toast message for the "off" case.
                            mNotificationManager.cancelAll();
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