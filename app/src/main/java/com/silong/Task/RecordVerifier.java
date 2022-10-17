package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.silong.dev.UserData;

import java.util.Timer;
import java.util.TimerTask;

public class RecordVerifier extends AsyncTask {

    private Activity activity;
    private int total;
    private long startTime;

    public RecordVerifier(Activity activity, int total){
        this.activity = activity;
        this.total = total;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        loop();
        return null;
    }

    private void loop(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("DEBUGGER>>>", "Entered loop: " + System.currentTimeMillis() + " - cur: " + UserData.pets.size());
                //check if processing time is more than 30secs
                if (System.currentTimeMillis()-startTime > 5000){
                    Log.d("DEBUGGER>>>", "timed out");
                    sendBroadcast("RV-download-complete");
                    //end task
                    return;
                }

                if (UserData.pets.size() == total)
                    sendBroadcast("RV-download-complete");
                else
                    loop();

            }
        }, 1000);
    }

    private void sendBroadcast(String code){
        Intent intent = new Intent(code);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

}
