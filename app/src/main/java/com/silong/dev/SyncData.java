package com.silong.dev;

public class SyncData {

    public SyncData(){

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
            //get pets only
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
