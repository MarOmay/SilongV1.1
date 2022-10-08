package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddressSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_settings);
        getSupportActionBar().hide();

    }

    public void onPressedDefault(View view){
        Toast.makeText(getApplicationContext(), "Silong is exclusive to San Jose del Monte City.", Toast.LENGTH_SHORT).show();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}