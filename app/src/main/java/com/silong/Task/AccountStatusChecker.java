package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.silong.Operation.Utility;
import com.silong.dev.DeactivatedScreen;
import com.silong.dev.Homepage;
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

            sendBroadcast("toggle-loading-dialog", true);

            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("accountSummary/" + UserData.userID);
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        boolean status = (Boolean) snapshot.getValue();
                        if (!status){

                            sendBroadcast("toggle-loading-dialog", false);

                            Intent intent = new Intent(activity, DeactivatedScreen.class);
                            intent.putExtra("uid", UserData.userID);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                        else {
                            if (Homepage.BEGIN_APPLY){
                                FirebaseDatabase rtdb = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                DatabaseReference ref = rtdb.getReference("Users").child(UserData.userID).child("cancellation");
                                //read cancellation value
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        sendBroadcast("toggle-loading-dialog", false);

                                        int curCount = Integer.parseInt(snapshot.getValue().toString());
                                        if (curCount < 3)
                                            sendBroadcast("account-status-active");
                                        else
                                            Toast.makeText(activity, "You have cancelled too many requests.", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        sendBroadcast("toggle-loading-dialog", false);
                                    }
                                });

                            }
                            else {
                                sendBroadcast("toggle-loading-dialog", false);
                            }
                        }
                    }
                    catch (Exception e){
                        Utility.log("ASC-dIB" + e.getMessage());
                        sendBroadcast("toggle-loading-dialog", false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    sendBroadcast("toggle-loading-dialog", false);
                }
            });
        }
        catch (Exception e){
            Utility.log("ASC-dIB" + e.getMessage());
            sendBroadcast("toggle-loading-dialog", false);
        }
        return null;
    }

    private void sendBroadcast(String code){
        Intent intent = new Intent(code);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    private void sendBroadcast(String code, boolean toggle){
        Intent intent = new Intent(code);
        intent.putExtra("toggle", toggle);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
