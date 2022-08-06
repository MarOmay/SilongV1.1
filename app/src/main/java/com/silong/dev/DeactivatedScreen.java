package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.AccountActivationDialog;
import com.silong.Operation.Utility;

public class DeactivatedScreen extends AppCompatActivity {

    TextView requestActivationTv;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivated_screen);
        getSupportActionBar().hide();

        //Initializze Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Dialog receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestActivation, new IntentFilter("request-activation"));

        requestActivationTv = (TextView) findViewById(R.id.requestActivationTv);

    }

    public void onRequestActivationPressed(View view){
        AccountActivationDialog accountActivationDialog = new AccountActivationDialog(DeactivatedScreen.this);
        accountActivationDialog.show();
    }

    private void sendRequest(){
        //Check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            try {
                mReference = mDatabase.getReference("Requests");
                //mReference. to be continued
            }
            catch (Exception e){
                Log.d("DS", e.getMessage());
                Toast.makeText(this, "Failed to send request.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver mRequestActivation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendRequest();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestActivation);
    }
}