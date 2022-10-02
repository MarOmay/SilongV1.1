package com.silong.Task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;
import com.silong.dev.HorizontalProgressBar;
import com.silong.dev.UserData;

import java.io.File;
import java.util.ArrayList;

public class SyncAdoptionHistory extends AsyncTask {

    private Activity activity;
    private ArrayList<String> keys = new ArrayList<>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DataSnapshot snapshotF;

    public  SyncAdoptionHistory(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mReference = mDatabase.getReference().child("Users").child(UserData.userID).child("adoptionHistory");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshotF = snapshot;

                try {
                    for (DataSnapshot snap : snapshotF.getChildren()){

                        //skip if key is null
                        if (snap == null || snap.getKey().equals("null") || snap.getKey() == null)
                            continue;

                        //skip if status is pending
                        int tempStatus = Integer.parseInt(snap.child("status").getValue().toString());
                        if (tempStatus > 0 && tempStatus < 7)
                            return;

                        //key is petID
                        Log.d("DEBUGGER>>>", "SAH: key-" + snap.getKey());

                        File file = new File(activity.getFilesDir(), "adoption-" + snap.getKey());
                        if (!file.exists()){
                            String key = snap.getKey();
                            String dateReq = snap.child("dateRequested").getValue().toString();
                            String status =  snap.child("status").getValue().toString();
                            fetchAdoptionFromCloud(key, dateReq, status);
                        }

                    }
                    UserData.populateAdoptions(activity);
                }
                catch (Exception e){
                    Log.d("SAH-dIB", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return null;
    }

    private void fetchAdoptionFromCloud(String id, String dateRequested, String status){
        DatabaseReference tempRef = mDatabase.getReference().child("Pets").child(id);
        tempRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    String gender = snapshot.child("gender").getValue().toString();
                    String type = snapshot.child("type").getValue().toString();
                    String age = snapshot.child("age").getValue().toString();
                    String color = snapshot.child("color").getValue().toString();
                    String size = snapshot.child("size").getValue().toString();
                    String photo = snapshot.child("photo").getValue().toString();

                    UserData.writeAdoptionToLocal(activity, id, "petID", id);
                    UserData.writeAdoptionToLocal(activity, id, "gender", gender);
                    UserData.writeAdoptionToLocal(activity, id, "type", type);
                    UserData.writeAdoptionToLocal(activity, id, "age", age);
                    UserData.writeAdoptionToLocal(activity, id, "color", color);
                    UserData.writeAdoptionToLocal(activity, id, "size", size);
                    UserData.writeAdoptionToLocal(activity, id, "dateRequested", dateRequested);
                    UserData.writeAdoptionToLocal(activity, id, "status", status);

                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(activity, bitmap, "adoptionpic-" + id);

                    UserData.populateAdoptions(activity);

                }
                catch (Exception e){
                    Log.d("SAH-fAFC", e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
