package com.silong.CustomView;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.silong.dev.R;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BarangaySpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private Context context;
    private String [] first, second;
    private String zip = "3023";

    public BarangaySpinner(Context context) {
        super(context);
        this.context = context;
        this.first = getResources().getStringArray(R.array.barangay_3023);
        this.second = getResources().getStringArray(R.array.barangay_3024);
        super.setAdapter(getArrayAdapter());
        super.setSelection(0);

    }

    public BarangaySpinner(@NonNull Context context, int mode) {
        super(context, mode);
        this.context = context;
        this.first = getResources().getStringArray(R.array.barangay_3023);
        this.second = getResources().getStringArray(R.array.barangay_3024);
        super.setAdapter(getArrayAdapter());
        super.setSelection(getArrayAdapter().getCount());
    }

    public BarangaySpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.first = getResources().getStringArray(R.array.barangay_3023);
        this.second = getResources().getStringArray(R.array.barangay_3024);
        super.setAdapter(getArrayAdapter());
        super.setSelection(0);

        this.setOnItemSelectedListener(onItemSelectedListener);
    }

    public int getPositionOf(String brgy){
        ArrayAdapter<String> arrayAdapter = getArrayAdapter();
        return arrayAdapter.getPosition(brgy);
    }

    public String getZip(){
        return zip;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
    }

    private ArrayAdapter<String> getArrayAdapter(){

        String[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);

        ArrayList<String> all = new ArrayList<>();
        for (String s : both){
            all.add(s);
        }

        all.sort(new Comparator<String>() {
            @Override
            public int compare(String b1, String b2) {
                return b1.compareTo(b2);
            }
        });

        both = new String[all.size()];

        for (int i=0; i<all.size(); i++){
            both[i] = all.get(i);
        }

        String[] finalBoth = both;

        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<String>(context, R.layout.drop_down_items, finalBoth) {
            @Override
            public int getCount() {
                return finalBoth.length-1;
            }
        };

        return barangayAdapter;
    }


    private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent("barangay-zip");
            String selectedBarangay = getSelectedItem().toString();

            for (String s : first){
                if (s.equals(selectedBarangay)){
                    intent.putExtra("zip", "3023");
                    zip = "3023";
                }
            }
            for (String s : second){
                if (s.equals(selectedBarangay)){
                    intent.putExtra("zip", "3024");
                    zip = "3024";
                }
            }

            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

}
