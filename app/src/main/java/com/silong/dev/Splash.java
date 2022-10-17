package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.WindowManager;

import com.silong.Operation.Utility;

public class Splash extends AppCompatActivity {
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            Utility.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            Utility.setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //For delaying, wala naman na sigurong papakilamanan dito ano? hahahaha
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Checks logged in user (Alex)
                    - if user.dat exists, proceed to Homepage
                    - else, require login or signup
                */
                Intent i;
                if (UserData.isLoggedIn(getApplicationContext())){
                    //UserData.populate();
                    i = new Intent(Splash.this, HorizontalProgressBar.class);
                }
                else {
                    i = new Intent(Splash.this, OnBoarding.class);
                }
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                finish();
            }
        }, 3000);
    }
}