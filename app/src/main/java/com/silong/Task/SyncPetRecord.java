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

import com.silong.Object.Pet;

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

    boolean skip = false;
    @Override
    protected Object doInBackground(Object[] objects) {

        if (HorizontalProgressBar.snapshot == null)
            return null;

        try {
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> list = new ArrayList<>();
            for (DataSnapshot snap : HorizontalProgressBar.snapshot.getChildren()){

                if (snap.getKey().equals("null") || snap.getKey() == null){
                    continue;
                }

                keys.add("pet-" + snap.getKey());

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
                    else {
                        //check last revision
                        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
                        mReference = mDatabase.getReference().child("Pets").child(snap.getKey()).child("lastModified");
                        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() == null){
                                    skip = true;
                                    return;
                                }

                                String lastModified = snapshot.getValue().toString();
                                Log.d("DEBUGGER>>>", "cur " + tempPet.getLastModified());
                                Log.d("DEBUGGER>>>", "new " + lastModified);
                                if (tempPet.getLastModified() != null){
                                    if (!tempPet.getLastModified().equals(lastModified)){
                                        //delete local record, to rewrite new record
                                        file.delete();
                                        fetchRecordFromCloud(snap.getKey());
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } //end of else

                }
                else {
                    fetchRecordFromCloud(snap.getKey());
                }

                list.add(snap.getKey());
            }

            //get pet- files only
            ArrayList<File> petFiles = new ArrayList<>();
            File [] files = activity.getFilesDir().listFiles();
            for (File file : files){
                if (file.getAbsolutePath().contains("pet-"))
                    petFiles.add(file);
            }

            for (File petFile : petFiles){
                boolean found = false;
                for (String key : keys){
                    File tempFile = new File(activity.getFilesDir(), key);
                    if (petFile.getAbsolutePath().equals(tempFile.getAbsolutePath()))
                        found = true;
                }

                //remove file if not found in key
                if (!found)
                    petFile.delete();
            }


            //delete local copy of deleted accounts
            cleanLocalRecord(list, "pet-");
            UserData.populateRecords(activity);
        }
        catch (Exception e){
            Log.d("SPR-dIB", e.getMessage());
        }
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
