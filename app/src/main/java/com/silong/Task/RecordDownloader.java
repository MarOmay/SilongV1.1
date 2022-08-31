package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.ImageProcessor;
import com.silong.dev.UserData;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class RecordDownloader extends AsyncTask {

    private Activity activity;
    private String id;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ArrayList<String> fragments = new ArrayList<>();

    public RecordDownloader(Activity activity, String id){
        this.activity = activity;
        this.id = id;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        try{
            //create local copy
            UserData.writePetToLocal(activity, id, "petID", id);

            DatabaseReference tempReference = mDatabase.getReference("Pets/" + id);
            tempReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = Integer.valueOf(snapshot.getValue().toString());
                    UserData.writePetToLocal(activity, id, "status", String.valueOf(status));
                    fragments.add("status");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int type = Integer.valueOf(snapshot.getValue().toString());
                    UserData.writePetToLocal(activity, id, "type", String.valueOf(type));
                    fragments.add("type");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int gender = Integer.valueOf(snapshot.getValue().toString());
                    UserData.writePetToLocal(activity, id, "gender", String.valueOf(gender));
                    fragments.add("gender");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("size").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int size = Integer.valueOf(snapshot.getValue().toString());
                    UserData.writePetToLocal(activity, id, "size", String.valueOf(size));
                    fragments.add("size");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("age").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int age = Integer.valueOf(snapshot.getValue().toString());
                    UserData.writePetToLocal(activity, id, "age", String.valueOf(age));
                    fragments.add("age");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("color").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String color = snapshot.getValue().toString();
                    UserData.writePetToLocal(activity, id, "color", String.valueOf(color));
                    fragments.add("color");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("lastModified").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String lastModified = snapshot.getValue().toString();
                    UserData.writePetToLocal(activity, id, "lastModified", lastModified);
                    fragments.add("lastModified");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("photo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String photo = snapshot.getValue().toString();
                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(activity, bitmap, "petpic-" + id);
                    UserData.populateRecords(activity);
                    fragments.add("photo");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("Homepage-fRFC", e.getMessage());
        }
        return null;
    }

    private void loop(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (fragments.size() == 8){
                    reloadUserData();
                }
                else
                    loop();
            }
        }, 1000);
    }

    public void reloadUserData(){
        UserData.populateRecords(activity);
    }

}
