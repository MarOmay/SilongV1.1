package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.silong.Task.PetCounter;
import com.silong.Task.RecordVerifier;
import com.silong.Task.SyncPetRecord;

public class HorizontalProgressBar extends AppCompatActivity {

    private LinearProgressIndicator linearProgressIndicator;
    private TextView horizontalProgressTv;

    public static DataSnapshot snapshot = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_progress_bar);
        getSupportActionBar().hide();

        //register receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnComplete, new IntentFilter("RV-download-complete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnFailure, new IntentFilter("RV-timed-out"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnCount, new IntentFilter("PC-count-complete"));

        linearProgressIndicator = findViewById(R.id.progressBar);
        horizontalProgressTv = (TextView) findViewById(R.id.horizontalProgressTv);

        //get total record count from RTDB
        PetCounter petCounter = new PetCounter(this);
        petCounter.execute();

    }

    private void verifyRecordCount(int total){
        RecordVerifier recordVerifier = new RecordVerifier(HorizontalProgressBar.this, total);
        recordVerifier.execute();
    }

    private BroadcastReceiver mOnComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //goto Homepage
            Intent i = new Intent(HorizontalProgressBar.this, Homepage.class);
            startActivity(i);
            finish();
        }
    };

    private BroadcastReceiver mOnFailure = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            horizontalProgressTv.setText("Please check your internet connection.");
        }
    };

    private BroadcastReceiver mOnCount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //run sync module
                SyncPetRecord syncPetRecord = new SyncPetRecord(HorizontalProgressBar.this);
                syncPetRecord.execute();

                String total = intent.getStringExtra("count");
                verifyRecordCount(Integer.parseInt(total));
            }
            catch (Exception e){
                Log.d("HPB", e.getMessage());
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnComplete);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnFailure);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnCount);
        super.onDestroy();
    }
}