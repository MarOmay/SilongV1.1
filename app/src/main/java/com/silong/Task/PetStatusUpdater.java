package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.EnumClass.PetStatus;

public class PetStatusUpdater extends AsyncTask {

    public static final String SUCCESS = "PSU-success";
    public static final String FAILED = "PSU-failed";

    private Activity activity;
    private String petID;
    private boolean active;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public PetStatusUpdater (Activity activity, String petID, boolean active){
        this.activity = activity;
        this.petID = petID;
        this.active = active;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference().child("Pets").child(petID).child("status");

            mReference.setValue(active ? PetStatus.ACTIVE : PetStatus.PROCESSING)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            DatabaseReference tempReference = mDatabase.getReference().child("recordSummary").child(petID);
                            tempReference.setValue(active ? PetStatus.ACTIVE : null);

                            sendBroadcast(SUCCESS);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sendBroadcast(FAILED);
                        }
                    });
        }
        catch (Exception e){
            Log.d("PSU-dIB", e.getMessage());
            sendBroadcast(FAILED);
        }

        return null;
    }

    private void sendBroadcast(String code){
        Intent intent = new Intent(code);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
