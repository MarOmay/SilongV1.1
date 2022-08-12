package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.dev.HorizontalProgressBar;

public class PetCounter extends AsyncTask {

    private Activity activity;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public PetCounter(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("DEBUGGER>>>", "Started counting");
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("recordSummary");
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int counter = 0;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        Log.d("DEBUGGER>>>", snap.getKey());
                        counter++;
                    }
                    HorizontalProgressBar.snapshot = snapshot;
                    sendBroadcast(counter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){

        }
        return null;
    }

    private void sendBroadcast(int count){
        Log.d("DEBUGGER>>>", "Broadcast count: " + count);
        Intent intent = new Intent("PC-count-complete");
        intent.putExtra("count", count+"");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
