package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.silong.CustomView.AccountActivationDialog;

public class DeactivatedScreen extends AppCompatActivity {

    TextView requestActivationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivated_screen);
        getSupportActionBar().hide();

        requestActivationTv = (TextView) findViewById(R.id.requestActivationTv);

    }

    public void onRequestActivationPressed(View view){
        AccountActivationDialog accountActivationDialog = new AccountActivationDialog(DeactivatedScreen.this);
        accountActivationDialog.show();
    }
}