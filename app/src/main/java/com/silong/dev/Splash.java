package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, LogIn.class);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                finish();
            }
        }, 3000);
    }
}