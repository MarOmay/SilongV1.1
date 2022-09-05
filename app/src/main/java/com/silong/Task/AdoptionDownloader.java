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

public class AdoptionDownloader extends AsyncTask {

    private Activity activity;
    private String date;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private ArrayList<String> fragments = new ArrayList<>();

    public AdoptionDownloader(Activity activity, String date){
        this.activity = activity;
        this.date = date;
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        try{
            //create local copy
            UserData.writeAdoptionToLocal(activity, date, "dateRequested", date);

            DatabaseReference tempReference = mDatabase.getReference("Users").child(UserData.userID).child("adoptionHistory").child(date);
            tempReference.child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = Integer.valueOf(snapshot.getValue().toString());
                    UserData.writeAdoptionToLocal(activity, date, "status", String.valueOf(status));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("petID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String petID = snapshot.getValue().toString();
                    UserData.writeAdoptionToLocal(activity, date, "petID", petID);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("appointmentDate").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String appointmentDate = snapshot.getValue().toString();
                    UserData.writeAdoptionToLocal(activity, date, "appointmentDate", appointmentDate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            tempReference.child("dateReleased").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dateReleased = snapshot.getValue().toString();
                    UserData.writeAdoptionToLocal(activity, date, "dateReleased", dateReleased);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        catch (Exception e){
            Log.d("Homepage-AD-dIB", e.getMessage());
        }
        return null;
    }
}
