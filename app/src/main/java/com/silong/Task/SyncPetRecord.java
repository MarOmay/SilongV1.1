package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;
import com.silong.dev.HorizontalProgressBar;
import com.silong.dev.UserData;

import java.io.File;
import java.util.ArrayList;

public class SyncPetRecord extends AsyncTask {

    private Activity activity;
    private ArrayList<String> keys = new ArrayList<>();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    public  SyncPetRecord(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        if (HorizontalProgressBar.snapshot == null)
            return null;

        try {
            ArrayList<String> list = new ArrayList<>();
            for (DataSnapshot snap : HorizontalProgressBar.snapshot.getChildren()){

                Log.d("DEBUGGER>>>", "SPR: key-" + snap.getKey());

                File file = new File(activity.getFilesDir(), "pet-" + snap.getKey());
                if (file.exists()){
                    //Check if status of local record matches
                    Pet tempPet = UserData.fetchRecordFromLocal(activity, snap.getKey());
                    if (tempPet.getStatus() != Integer.valueOf(snap.getValue().toString())){
                        //delete local record, to rewrite new record
                        file.delete();
                        fetchRecordFromCloud(snap.getKey());
                    }
                }
                else {
                    fetchRecordFromCloud(snap.getKey());
                }
                list.add("pet-" + snap.getKey());
            }
            //delete local copy of deleted accounts
            cleanLocalRecord(list, "pet-");
            UserData.populateRecords(activity);
        }
        catch (Exception e){
            Log.d("SPR-dIB", e.getMessage());
        }

        /*if (Utility.internetConnection(activity)){
            //Get all pet records
            mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            mReference = mDatabase.getReference("Pets");
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        //Get all User uid
                        ArrayList<String> list = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()){

                            //skip counter to avoid error
                            if (snap.getKey().equals("counter"))
                                continue;

                            File file = new File(activity.getFilesDir(), "pet-" + snap.getKey());
                            if (file.exists()){
                                //Check if status of local record matches
                                Pet tempPet = UserData.fetchRecordFromLocal(activity, snap.getKey());
                                if (tempPet.getStatus() != Integer.valueOf(snap.getValue().toString())){
                                    //delete local record, to rewrite new record
                                    file.delete();
                                    fetchRecordFromCloud(snap.getKey());
                                }
                            }
                            else {
                                fetchRecordFromCloud(snap.getKey());
                            }
                            list.add("pet-" + snap.getKey());
                        }
                        //delete local copy of deleted accounts
                        cleanLocalRecord(list, "pet-");
                        UserData.populateRecords(activity);
                    }
                    catch (Exception e){
                        Log.d("Homepage-fAR", e.getMessage());
                    }
                    UserData.populateRecords(activity);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    UserData.populateRecords(activity);
                }
            });
        }
        else {
            Toast.makeText(activity, "No internet connection.", Toast.LENGTH_SHORT).show();
            UserData.populateRecords(activity);
        }*/
        return null;
    }

    private void fetchRecordFromCloud(String id){
        RecordDownloader downloader = new RecordDownloader(activity, id);
        downloader.execute();
    }

    private void cleanLocalRecord(ArrayList<String> list, String prefix){
        File [] files = activity.getFilesDir().listFiles();
        ArrayList<File> accountFiles = new ArrayList<>();

        //filter out non-account files
        for (File file : files){
            if (file.getAbsolutePath().contains(prefix)){
                accountFiles.add(file);
            }
        }

        //filter deleted accounts
        ArrayList<File> deletedAccounts = new ArrayList<>();
        for (File file : accountFiles){
            boolean found = false;
            for (String s : list){
                if (file.getAbsolutePath().contains(s))
                    found = true;
            }
            if (!found)
                deletedAccounts.add(file);
        }

        for (File file : deletedAccounts){
            try {
                file.delete();
            }
            catch (Exception e){
                Log.d("Homepage-cLR", e.getMessage());
            }
        }
    }
}
