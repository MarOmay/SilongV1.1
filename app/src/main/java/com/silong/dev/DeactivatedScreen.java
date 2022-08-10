package com.silong.dev;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.AccountActivationDialog;
import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;

public class DeactivatedScreen extends AppCompatActivity {

    TextView requestActivationTv;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivated_screen);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Dialog receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRequestActivation, new IntentFilter("request-activation"));

        uid = getIntent().getStringExtra("uid");

        requestActivationTv = (TextView) findViewById(R.id.requestActivationTv);

    }

    public void onRequestActivationPressed(View view){
        AccountActivationDialog accountActivationDialog = new AccountActivationDialog(DeactivatedScreen.this);
        accountActivationDialog.show();
    }

    private void sendRequest(String uid, String message){
        //Check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("type", "activation");
                map.put("reason", message);

                mReference = mDatabase.getReference("accountStatusRequests");
                mReference.child(uid).updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(DeactivatedScreen.this, "Request sent.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(DeactivatedScreen.this, "Please wait for an admin to review your request.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DeactivatedScreen.this, "Request not sent.", Toast.LENGTH_SHORT).show();
                            }
                        });
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
            sendRequest(uid, intent.getStringExtra("reason"));
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRequestActivation);
    }
}