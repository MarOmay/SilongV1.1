package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HorizontalProgressBar extends AppCompatActivity {

    TextView horizontalProgressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_progress_bar);
        getSupportActionBar().hide();

        horizontalProgressTv = (TextView) findViewById(R.id.horizontalProgressTv);
    }
}