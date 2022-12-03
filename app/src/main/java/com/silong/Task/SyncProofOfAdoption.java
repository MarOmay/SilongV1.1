package com.silong.Task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.Adoption;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.dev.Timeline;
import com.silong.dev.UserData;

import java.io.File;

public class SyncProofOfAdoption extends AsyncTask {

    private Activity activity;
    private String userID;

    public SyncProofOfAdoption(Activity activity, String userID){
        this.activity = activity;
        this.userID = userID;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {

            for (Adoption adoption : UserData.adoptionHistory){

                int petID = Integer.parseInt(adoption.getPetID());

                if (!(adoption.getStatus() == Timeline.ADOPTION_SUCCESSFUL || adoption.getStatus() == Timeline.FINISHED)){
                    continue;
                }

                File file = new File(activity.getFilesDir(),"poa-" + petID);

                //check if poa exists
                if (!file.exists()){
                    //try to download
                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference mRef = mDatabase.getReference("Users/" + userID + "/proofOfAdoption/" + petID);

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Object poa = snapshot.getValue();

                            if (poa != null){
                                Bitmap bitmap = new ImageProcessor().toBitmap(poa.toString());
                                new ImageProcessor().saveToLocal(activity, bitmap, "poa-" + petID);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Utility.log("SPA.dIB.oC: " + error.getMessage());
                        }
                    });
                }
            }
        }
        catch (Exception e){
            Utility.log("SPOA.dIB: " + e.getMessage());
        }

        return null;
    }
}
