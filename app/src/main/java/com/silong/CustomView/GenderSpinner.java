package com.silong.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.silong.dev.R;

public class GenderSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private Context context;


    public GenderSpinner(Context context) {
        super(context);
        this.context = context;
        super.setAdapter(getArrayAdapter());
        super.setSelection(getArrayAdapter().getCount());
    }

    public GenderSpinner(Context context, int mode) {
        super(context, mode);
        this.context = context;
        super.setAdapter(getArrayAdapter());
        super.setSelection(getArrayAdapter().getCount());
    }

    public GenderSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        super.setAdapter(getArrayAdapter());
        super.setSelection(getArrayAdapter().getCount());
    }

    private ArrayAdapter<String> getArrayAdapter(){

        String[] gen = getResources().getStringArray(R.array.Gender);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(context, R.layout.drop_down_items, gen) {
            @Override
            public int getCount() {
                return gen.length-1;
            }
        };

        return genderAdapter;
    }


}
