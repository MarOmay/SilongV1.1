package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.silong.CustomView.HomepageExitDialog;

public class UnderMaintenanceScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_maintenance_screen);
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        HomepageExitDialog homepageExitDialog = new HomepageExitDialog(UnderMaintenanceScreen.this);
        homepageExitDialog.show();
    }
}