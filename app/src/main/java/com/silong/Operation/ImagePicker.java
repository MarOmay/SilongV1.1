package com.silong.Operation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.silong.dev.MainActivity;

public class ImagePicker {

    private final int PERMISSION_REQUEST_STORAGE = 0;

    private Activity activity;
    private int requestCode;

    public ImagePicker(Activity activity, int requestCode){
        this.activity = activity;
        this.requestCode = requestCode;

        //Check permissions first before starting intent chooser
        if (requestPermission()){
            startPicker();
        }
    }
    
    public void startPicker(){
        /* Create image chooser
        *   onActivityResult must be defined separately in their respective Activities
        * */

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Choose an image"), requestCode);

    }

    private boolean requestPermission(){
        /* Checks the following permissions:
        * - READ_EXTERNAL_STORAGE
        *
        * Doesn't check the following permissions:
        * - WRITE_INTERNAL_STORAGE
        * - MANAGE_ALL_FILES_ACCESS
        *
        * Note: onRequestPermissionsResult must be defined separately in their respective Activities
        * */

        //Check permissions
        if(ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity.getApplicationContext(), "Silong needs READ access to upload photo", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivity.READ_EXTERNAL_STORAGE_REQUEST);
            return false;
        }

        return true;
    }

}
