package com.silong.Task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.Utility;

public class CancellationCounter extends AsyncTask {

    private String uid;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public CancellationCounter(String uid){
        this.uid = uid;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("Users").child(uid).child("cancellation");

            //get current value
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int curCount = Integer.parseInt(snapshot.getValue().toString());


                    //set new value
                    mReference.setValue(++curCount);
                    Utility.log("Setting new cancellation value: " + curCount);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Utility.log("CC.dIB: " + e.getMessage());
        }

        return null;
    }
}
