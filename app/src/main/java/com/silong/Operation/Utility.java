package com.silong.Operation;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.dev.Homepage;
import com.silong.dev.LoadingDialog;
import com.silong.dev.R;
import com.silong.dev.Timeline;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public final static int STORAGE_REQUEST_CODE = 3;

    public static final String CLAUSE_SEPARATOR = "#CLAUSE-SEPARATOR#";

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static boolean internetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return true;
        }
        return false;
    }

    public void passwordFieldTransformer(EditText field, boolean visible){
        field.setTransformationMethod(visible ? null : new PasswordTransformationMethod());
    }

    public static String dateToday(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String timeNow(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH*mm*ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public static void log(String message){
        if (message.length() < 1)
            return;
        else
            Log.d("DEBUGGER>>>", message);
    }

    private static void launchMessenger(Activity activity, String fbID){

        if (fbID == null){
            fbID = "CityVetOfficeCSJDM";
        }

        try{
            //open FB messenger
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setPackage("com.facebook.orca");
            intent.setData(Uri.parse("https://m.me/"+fbID));
            activity.startActivity(intent);
        }
        catch (Exception e){

            //open FB page
            try {
                //using fb app
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/CityVetOfficeCSJDM"));
                activity.startActivity(intent);
            }
            catch (Exception ex){
                //using browser
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/CityVetOfficeCSJDM")));
            }

        }

    }

    public static void gotoMessenger(Activity activity){
        LoadingDialog loadingDialog = new LoadingDialog(activity);
        loadingDialog.startLoadingDialog();

        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mRef = mDatabase.getReference("publicInformation").child("contactInformation").child("facebookPage");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loadingDialog.dismissLoadingDialog();
                    if (snapshot.getValue() != null){
                        String fbid = snapshot.getValue().toString();
                        launchMessenger(activity, fbid);
                    }
                    else {
                        launchMessenger(activity, null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingDialog.dismissLoadingDialog();
                    Utility.log("Utility.fF: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            loadingDialog.dismissLoadingDialog();
            Utility.log("Utility.fF: " + e.getMessage());
        }
    }

    public static void animateOnClick(Context context, View view){
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.custom_on_click));
    }

    public static boolean requestPermission(Activity activity, int requestCode){

        //Check WRITE permissions
        if(ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(activity.getApplicationContext(), "Silong needs WRITE access to export file.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            return false;
        }

        return true;
    }

    public void showNotification(Context context, String title, String message) {
        //SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(context);
        Intent intent = new Intent(context, context.getClass());
        int reqCode = 101;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_IMMUTABLE);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.silong_user_app_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id

        Log.d("showNotification", "showNotification: " + reqCode);
    }

}
