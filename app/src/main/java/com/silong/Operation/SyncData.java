package com.silong.Operation;

import android.content.Context;

import java.io.File;

public class SyncData {

    private final Context context;

    public SyncData(Context context){
        this.context = context;
    }

    public void syncAll(boolean admin){
        syncProfiles(admin);
        syncRequests(admin);
        syncMessages(admin);
    }

    public void syncProfiles(boolean admin){

        if (admin){
            //get all
        }
        else {
            File file = new File(context.getFilesDir(),"user.dat");

            //check if user.dat exists to read user info
            if(file.exists()){
                try{

                }
                catch (Exception e){

                }
            }
            else {
                //route user to login screen
            }
        }

        //get all existing, then filter out the existing data to fetch the non-existent only

        //prepare request inside if-else first, then pass the value to thread

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void syncRequests(boolean admin){

        if (admin){
            //get all active requests
        }
        else {
            //get all requests (active/inactive) specific to user
        }

        //prepare request inside if-else first, then pass the value to thread

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void syncMessages(boolean admin){

        if (admin){
            //get all messages
        }
        else {
            //get all messages (active/inactive) specific to user
        }

        //prepare request inside if-else first, then pass the value to thread

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

}
