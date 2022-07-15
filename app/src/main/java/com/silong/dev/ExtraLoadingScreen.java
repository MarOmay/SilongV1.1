package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ExtraLoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_loading_screen);
        getSupportActionBar().hide();
    }
}