package com.silong.CustomView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Context context;

    public DatePickerFragment(Context context){
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        c.add(Calendar.YEAR, -18);
        dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        long minimum = (long) (c.getTimeInMillis() - (1000 * 60 * 60 * 24 * 365.25 * 42));
        dialog.getDatePicker().setMinDate(minimum);
        return  dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Intent intent = new Intent("update-date");
        intent.putExtra("date", month+1+"/"+day+"/"+year);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
