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



public class RecordDownloader extends AsyncTask {

    private Activity activity;
    private String id;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public RecordDownloader(Activity activity, String id){
        this.activity = activity;
        this.id = id;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        DatabaseReference tempReference = mDatabase.getReference("Pets").child(id);
        tempReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    int status = Integer.valueOf(snapshot.child("status").getValue().toString());
                    int type = Integer.valueOf(snapshot.child("type").getValue().toString());
                    int gender = Integer.valueOf(snapshot.child("gender").getValue().toString());
                    int size = Integer.valueOf(snapshot.child("size").getValue().toString());
                    int age = Integer.valueOf(snapshot.child("age").getValue().toString());
                    String color = snapshot.child("color").getValue().toString();
                    String lastModified = snapshot.child("lastModified").getValue().toString();
                    String photo = snapshot.child("photo").getValue().toString();

                    //get optional data
                    String distMark = null, rescueDate = null, extrapic1 = null, extrapic2 = null;
                    try {
                        distMark = snapshot.child("distMark").getValue().toString();
                    }
                    catch (Exception e){
                        Utility.log("RecordDownloader: Check if exist distMark - " + e.getMessage());
                    }
                    try {
                        rescueDate = snapshot.child("rescueDate").getValue().toString();
                    }
                    catch (Exception e){
                        Utility.log("RecordDownloader: Check if exist rescueDate - " + e.getMessage());
                    }
                    try {
                        extrapic1 = snapshot.child("extraPhoto").child("photo1").getValue().toString();
                    }
                    catch (Exception e){
                        Utility.log("RecordDownloader: Check if exist extrapic1 - " + e.getMessage());
                    }
                    try {
                        extrapic2 = snapshot.child("extraPhoto").child("photo2").getValue().toString();
                    }
                    catch (Exception e){
                        Utility.log("RecordDownloader: Check if exist extrapic2 - " + e.getMessage());
                    }

                    //create local copy
                    UserData.writePetToLocal(activity, id, "petID", id);
                    UserData.writePetToLocal(activity, id, "status", String.valueOf(status));
                    UserData.writePetToLocal(activity, id, "type", String.valueOf(type));
                    UserData.writePetToLocal(activity, id, "gender", String.valueOf(gender));
                    UserData.writePetToLocal(activity, id, "size", String.valueOf(size));
                    UserData.writePetToLocal(activity, id, "age", String.valueOf(age));
                    UserData.writePetToLocal(activity, id, "color", String.valueOf(color));
                    UserData.writePetToLocal(activity, id, "lastModified", lastModified);

                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    new ImageProcessor().saveToLocal(activity, bitmap, "petpic-" + id);

                    //write extra to local
                    if (distMark != null){
                        UserData.writePetToLocal(activity, id, "distMark", String.valueOf(distMark));
                    }
                    if (rescueDate != null){
                        UserData.writePetToLocal(activity, id, "rescueDate", String.valueOf(rescueDate));
                    }
                    if (extrapic1 != null){
                        Bitmap bmp = new ImageProcessor().toBitmap(extrapic1);
                        new ImageProcessor().saveToLocal(activity, bmp, "extrapic-" + id + "-1");
                    }
                    if (extrapic2 != null){
                        Bitmap bmp = new ImageProcessor().toBitmap(extrapic2);
                        new ImageProcessor().saveToLocal(activity, bmp, "extrapic-" + id + "-2");
                    }

                    UserData.populateRecords(activity);
                }
                catch (Exception e){
                    Log.d("DEBUGGER>>>", e.getMessage());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return null;
    }

}
