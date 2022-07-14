package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogIn extends AppCompatActivity {

    Button signUp, logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        signUp = (Button) findViewById(R.id.btnSignup);
        logIn = (Button) findViewById(R.id.btnLogin);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}