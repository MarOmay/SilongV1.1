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
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.dev.UserData;

import java.io.File;
import java.util.ArrayList;

public class SyncPetInprocess extends AsyncTask {

    private Activity activity;
    private ArrayList<String> keys = new ArrayList<>();

    public SyncPetInprocess(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference mRef = mDatabase.getReference("adoptionRequest");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snap : snapshot.getChildren()){

                    Object petID = snap.child("petID").getValue();
                    Object status = snap.child("status").getValue();

                    if (petID != null && status != null){

                        File data = new File(activity.getFilesDir(), "inprocess-" + petID);
                        File pict = new File(activity.getFilesDir(), "inprocesspic-" + petID);

                        int instanceStatus = Integer.parseInt(status.toString());

                        if (instanceStatus >= 1 && instanceStatus <= 4){
                            if (!data.exists()){
                                fetchRecordFromCloud(petID.toString());
                            }
                        }
                        //delete local copy
                        else {
                            if (data.exists()){
                                data.delete();
                                pict.delete();
                            }
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.log("SPIp.dIB.oC: " + error.getMessage());
            }
        });

        return null;
    }

    private void fetchRecordFromCloud(String petID){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference mRef = mDatabase.getReference("Pets/"+petID);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int status = Integer.valueOf(snapshot.child("status").getValue().toString());
                    int type = Integer.valueOf(snapshot.child("type").getValue().toString());
                    int gender = Integer.valueOf(snapshot.child("gender").getValue().toString());
                    int size = Integer.valueOf(snapshot.child("size").getValue().toString());
                    int age = Integer.valueOf(snapshot.child("age").getValue().toString());
                    String color = snapshot.child("color").getValue().toString();
                    String photo = snapshot.child("photo").getValue().toString();

                    //create local copy
                    UserData.writeInprocessPetToLocal(activity, petID, "petID", petID);
                    UserData.writeInprocessPetToLocal(activity, petID, "status", String.valueOf(status));
                    UserData.writeInprocessPetToLocal(activity, petID, "type", String.valueOf(type));
                    UserData.writeInprocessPetToLocal(activity, petID, "gender", String.valueOf(gender));
                    UserData.writeInprocessPetToLocal(activity, petID, "size", String.valueOf(size));
                    UserData.writeInprocessPetToLocal(activity, petID, "age", String.valueOf(age));
                    UserData.writeInprocessPetToLocal(activity, petID, "color", String.valueOf(color));

                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(activity, bitmap, "inprocesspic-" + petID);

                }
                catch (Exception e){
                    Utility.log("SPIp.fRFC.oDC: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.log("SPIp.fRFC.oC: " + error.getMessage());
            }
        });
    }
}
