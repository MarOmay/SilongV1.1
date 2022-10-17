package com.silong.CustomView;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.silong.dev.R;
import com.silong.dev.Timeline;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class AppointmentTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerDialog tpd;

    public void onCreate(@NonNull Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        tpd = TimePickerDialog.newInstance(AppointmentTimePickerFragment.this, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);

        tpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.pink));
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                tpd.setThemeDark(true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                tpd.setThemeDark(false);
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                tpd.setThemeDark(true);
                break;
        }

        tpd.setTimeInterval(1, 5);
        tpd.setMinTime(8, 60, 60);
        tpd.setMaxTime(16, 60, 60);
        tpd.show(requireFragmentManager(), "Datepickerdialog");

        tpd.setCancelable(false);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hour, int minute, int second) {
        boolean isPM = (hour >= 12);
        String time = String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM");
        tpd = null;
        Toast.makeText(getContext(), time, Toast.LENGTH_SHORT).show();
        Timeline.CHOSEN_TIME = time;
        this.dismiss();

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("schedule-chosen"));
    }

}
