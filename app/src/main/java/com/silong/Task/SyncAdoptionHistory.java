package com.silong.Task;

import android.app.Activity;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            for (DataSnapshot snap : snapshotF.getChildren()){

                //key is dateRequested
                Log.d("DEBUGGER>>>", "SAH: key-" + snap.getKey());

                File file = new File(activity.getFilesDir(), "adoption-" + snap.getKey());
                if (file.exists()){
                    //Check if status of local record matches
                    Adoption tempAdoption = UserData.fetchAdoptionFromLocal(activity, snap.getKey());
                    if (!String.valueOf(tempAdoption.getStatus()).equals(snap.getValue().toString()) ){
                        //delete local record, to rewrite new record
                        file.delete();
                        fetchAdoptionFromCloud(snap.getKey());
                    }

                }
                else {
                    fetchAdoptionFromCloud(snap.getKey());
                }
            }
            UserData.populateAdoptions(activity);
        }
        catch (Exception e){
            Log.d("SPR-dIB", e.getMessage());
        }
        return null;
    }

    private void fetchAdoptionFromCloud(String id){
        //RecordDownloader downloader = new RecordDownloader(activity, id);
        //downloader.execute();
    }
}
