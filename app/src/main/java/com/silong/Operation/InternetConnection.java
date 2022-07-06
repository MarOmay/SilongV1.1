package com.silong.Operation;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class InternetConnection {

    private final Context context;

    public InternetConnection(Context context) {
        this.context = context;
    }

    public boolean check(){

        try{
            URLConnection conn = new URL("https://firebase.google.com").openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        }
        catch (Exception e){
            Toast.makeText(context, "Database server unreachable", Toast.LENGTH_LONG).show();
            //goto connection error screen
        }

        try{
            URLConnection conn = new URL("https://www.google.com").openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        }
        catch (Exception e){
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            //goto connection error screen
        }


        return false;
    }
}
