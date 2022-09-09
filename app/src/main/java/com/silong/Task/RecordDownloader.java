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
import com.silong.dev.UserData;

import java.util.ArrayList;

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
