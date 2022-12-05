package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.silong.Operation.Utility;
import com.silong.Task.PetCounter;
import com.silong.Task.RecordVerifier;

import com.silong.Task.SyncAdoptionHistory;
import com.silong.Task.SyncPetInprocess;
import com.silong.Task.SyncPetRecord;
import com.silong.Task.SyncProofOfAdoption;

import java.io.File;

public class HorizontalProgressBar extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearProgressIndicator linearProgressIndicator;
    private TextView horizontalProgressTv;

    public static DataSnapshot snapshot = null;

    public static boolean syncAdoptionDone = false, petCounterDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal_progress_bar);
        getSupportActionBar().hide();

        syncAdoptionDone = false;
        petCounterDone = false;

        //register receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnFailure, new IntentFilter("RV-timed-out"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnCount, new IntentFilter("PC-count-complete"));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        linearProgressIndicator = findViewById(R.id.progressBar);
        horizontalProgressTv = (TextView) findViewById(R.id.horizontalProgressTv);

        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        //check internet connection
        if (!Utility.internetConnection(HorizontalProgressBar.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            horizontalProgressTv.setText("No internet connection.");
            return;
        }

        try {
            //check if user is logged in
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            UserData.populate(HorizontalProgressBar.this);

            UserData.populateAdoptions(HorizontalProgressBar.this);

            //get total record count from RTDB
            PetCounter petCounter = new PetCounter(this);
            petCounter.execute();

            //sync adoption record
            SyncAdoptionHistory syncAdoptionHistory = new SyncAdoptionHistory(HorizontalProgressBar.this, userID,true);
            syncAdoptionHistory.execute();

            //sync pet in process
            SyncPetInprocess syncPetInprocess = new SyncPetInprocess(HorizontalProgressBar.this);
            syncPetInprocess.execute();

            //sync proof of adoption
            SyncProofOfAdoption syncProofOfAdoption = new SyncProofOfAdoption(HorizontalProgressBar.this, userID);
            syncProofOfAdoption.execute();
        }
        catch (Exception e){
            Utility.log("HPB: " + e.getMessage());

            //remove user data
            Homepage.USERDATA = new File(getFilesDir(),"user.dat");
            Homepage.AVATARDATA = new File(getFilesDir(),"avatar.dat");
            UserData.logout(HorizontalProgressBar.this);

            //return to splash
            Intent intent = new Intent(HorizontalProgressBar.this, Splash.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            finish();
        }


    }

    private void verifyRecordCount(int total){
        RecordVerifier recordVerifier = new RecordVerifier(HorizontalProgressBar.this, total);
        recordVerifier.execute();
    }

    public static void checkCompletion(Activity activity){
        if (syncAdoptionDone && petCounterDone){
            //goto Landing Page
            Intent i = new Intent(activity, LandingPage.class);
            activity.startActivity(i);
            activity.overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            activity.finish();
        }
    }

    public static void logout(Activity activity){
        //remove user data
        Homepage.USERDATA = new File(activity.getFilesDir(),"user.dat");
        Homepage.AVATARDATA = new File(activity.getFilesDir(),"avatar.dat");
        UserData.logout(activity);

        //return to splash
        Intent intent = new Intent(activity, Splash.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        activity.finish();
    }

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

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            Intent intent = new Intent(HorizontalProgressBar.this, HorizontalProgressBar.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            swipeRefreshLayout.setRefreshing(false);
            finish();

        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnFailure);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnCount);
        super.onDestroy();
    }
}