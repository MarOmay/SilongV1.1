package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.silong.Object.Adoption;
import com.silong.Operation.ImageProcessor;

import com.silong.Operation.Utility;
import com.silong.dev.Homepage;
import com.silong.dev.HorizontalProgressBar;
import com.silong.dev.UserData;

import java.io.File;
import java.util.ArrayList;

public class SyncAdoptionHistory extends AsyncTask {

    private Activity activity;
    private boolean routeOnFinish;
    private String userID;
    private ArrayList<String> keys = new ArrayList<>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DataSnapshot snapshotF;

    public  SyncAdoptionHistory(Activity activity, String userID, boolean routeOnFinish){
        this.activity = activity;
        this.routeOnFinish = routeOnFinish;
        this.userID = userID;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mReference = mDatabase.getReference().child("Users").child(userID).child("adoptionHistory");
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshotF = snapshot;

                try {


                    ArrayList<String> keys = new ArrayList<>();
                    for (DataSnapshot snap : snapshotF.getChildren()){

                        //skip if key is null
                        if (snap == null || snap.getKey().equals("null") || snap.getKey() == null)
                            continue;

                        keys.add(snap.getKey());
                        /*
                        //skip if status is pending

                        if (tempStatus > 0 && tempStatus < 6)
                            return;*/

                        //key is petID
                        Log.d("DEBUGGER>>>", "SAH: key-" + snap.getKey());

                        try{
                            int tempStatus = Integer.parseInt(snap.child("status").getValue().toString());

                            String key = snap.getKey();
                            String dateReq = snap.child("dateRequested").getValue().toString();
                            String status =  snap.child("status").getValue().toString();

                            File file = new File(activity.getFilesDir(), "adoption-" + snap.getKey());
                            if (!file.exists()){
                                fetchAdoptionFromCloud(key, dateReq, status);
                                Homepage.RESTART_REQUIRED = true;
                            }
                            else {
                                Adoption adoption = UserData.fetchAdoptionFromLocal(activity, snap.getKey());
                                if (adoption.getStatus() != tempStatus || adoption.getPetID() == null){
                                    fetchAdoptionFromCloud(key, dateReq, status);
                                    Homepage.RESTART_REQUIRED = true;
                                }

                            }

                            int statusInt = Integer.parseInt(status);
                            if (statusInt >= 1 && statusInt <= 5){
                                fetchAdoptionRequest();
                            }
                        }
                        catch (Exception e){
                            Utility.log("SAH.dIB: " + e.getMessage());
                        }

                    }

                    //delete surplus data
                    for (File file : activity.getFilesDir().listFiles()){
                        if (file.getAbsolutePath().contains("adoption-") && !file.getAbsolutePath().contains("null")){
                            boolean found = false;
                            for (String key : keys){
                                String[] path = file.getAbsolutePath().split("-");
                                if (path[path.length-1].equals(key)){
                                    found = true;
                                }
                            }
                            if (!found){
                                file.delete();
                            }
                        }
                    }

                    UserData.populateAdoptions(activity);

                    if (routeOnFinish){
                        HorizontalProgressBar.syncAdoptionDone = true;
                        HorizontalProgressBar.checkCompletion(activity);
                    }
                    else {
                        LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent("SAH-done"));
                    }

                }
                catch (Exception e){
                    Utility.log("SAH.dIB: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.log("SAH.dIB.OC: " + error.getMessage());
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


                    UserData.writeAdoptionToLocal(activity, id, "petID", id);
                    UserData.writeAdoptionToLocal(activity, id, "gender", gender);
                    UserData.writeAdoptionToLocal(activity, id, "type", type);
                    UserData.writeAdoptionToLocal(activity, id, "age", age);
                    UserData.writeAdoptionToLocal(activity, id, "color", color);
                    UserData.writeAdoptionToLocal(activity, id, "size", size);
                    UserData.writeAdoptionToLocal(activity, id, "dateRequested", dateRequested);
                    UserData.writeAdoptionToLocal(activity, id, "status", status);

                    File file = new File(activity.getFilesDir(), "adoptionpic-" + id);
                    if (!file.exists()){
                        String photo = snapshot.child("photo").getValue().toString();
                        Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                        new ImageProcessor().saveToLocal(activity, bitmap, "adoptionpic-" + id);
                    }

                    UserData.populateAdoptions(activity);

                }
                catch (Exception e){
                    Utility.log("SAH.fAFC: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.log("SAH.fAFC.oC: " + error.getMessage());
            }
        });
    }

    private void fetchAdoptionRequest(){

        DatabaseReference tempRef = mDatabase.getReference("adoptionRequest").child(userID);
        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean falseAlarm = true;

                try {
                    String petID = snapshot.child("petID").getValue().toString();
                    int pid = Integer.parseInt(petID);
                    if (pid >= 3 && pid <= 5){
                        falseAlarm = false;
                    }
                    String appointmentDate = snapshot.child("appointmentDate").getValue().toString();
                    String appointmentTime = snapshot.child("appointmentTime").getValue().toString();

                    UserData.writeAdoptionToLocal(activity, petID, "appointmentDate", appointmentDate + " " + appointmentTime);
                    //UserData.writeAdoptionToLocal(activity, petID, "appointmentTime", appointmentTime);
                }
                catch (Exception e){
                    Utility.log("SAH.fAR: (" + (falseAlarm ? "falseAlarm" : "not falseAlarm") + ") : " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.log("SAH.fAR.oC: " + error.getMessage());
            }
        });
    }
}
