package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UnderMaintenanceScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_maintenance_screen);
        getSupportActionBar().hide();
    }
}