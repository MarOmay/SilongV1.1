package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.dev.DeactivatedScreen;
import com.silong.dev.UserData;

public class AccountStatusChecker extends AsyncTask {

    private Activity activity;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public AccountStatusChecker(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("accountSummary/" + UserData.userID);
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        boolean status = (Boolean) snapshot.getValue();
                        if (!status){
                            Intent intent = new Intent(activity, DeactivatedScreen.class);
                            intent.putExtra("uid", UserData.userID);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                    catch (Exception e){
                        Log.d("ASC-dIB", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            Log.d("ASC-dIB", e.getMessage());
        }
        return null;
    }
}
