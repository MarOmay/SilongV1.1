package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.Address;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.SyncData;
import com.silong.Task.SyncPetRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class LoginLoadingScreen extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseDatabase database;
    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReferenceAdmin;

    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_loading_screen);
        getSupportActionBar().hide();

        String uid = getIntent().getStringExtra("UID");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        database = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Identify if User or Admin account
        boolean user = false;
        try {
            databaseReferenceUser = database.getReference("Users/" + uid);
            user = true;
        } catch (Exception e){
            Log.d("USERTYPE:", "Not user");
        }

        //Retrieve data from database
        if (user){

            UserData.userID = uid;
            new ImageProcessor().saveToLocal(getApplicationContext(), "userID", UserData.userID);

            //Get account status, if deactivated notify user
            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        UserData.accountStatus = (Boolean) snapshot.child("accountStatus").getValue();
                        UserData.email = snapshot.child("email").getValue().toString();
                        UserData.firstName = snapshot.child("firstName").getValue().toString();
                        UserData.lastName = snapshot.child("lastName").getValue().toString();
                        UserData.birthday = snapshot.child("birthday").getValue().toString();
                        UserData.gender = Integer.parseInt(snapshot.child("gender").getValue().toString());
                        UserData.contact = snapshot.child("contact").getValue().toString();
                        String photo = snapshot.child("photo").getValue().toString();
                        UserData.address.setAddressLine(snapshot.child("address").child("addressLine").getValue().toString());
                        UserData.address.setBarangay(snapshot.child("address").child("barangay").getValue().toString());
                        UserData.address.setMunicipality(snapshot.child("address").child("municipality").getValue().toString());
                        UserData.address.setProvince(snapshot.child("address").child("province").getValue().toString());
                        UserData.address.setZipcode(Integer.parseInt(snapshot.child("address").child("zipcode").getValue().toString()));

                        new ImageProcessor().saveToLocal(getApplicationContext(), "accountStatus", UserData.accountStatus?"true":"false");
                        new ImageProcessor().saveToLocal(getApplicationContext(), "email", UserData.email);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "firstName", UserData.firstName);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "lastName", UserData.lastName);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "birthday", UserData.birthday);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "gender", UserData.gender+"");
                        new ImageProcessor().saveToLocal(getApplicationContext(), "contact", UserData.contact);
                        new ImageProcessor().saveToLocal(getApplicationContext(), "adoptionCounter", UserData.adoptionCounter+"");
                        new ImageProcessor().saveToLocal(getApplicationContext(), "addressLine", UserData.address.getAddressLine());
                        new ImageProcessor().saveToLocal(getApplicationContext(), "barangay", UserData.address.getBarangay());
                        new ImageProcessor().saveToLocal(getApplicationContext(), "municipality", UserData.address.getMunicipality());
                        new ImageProcessor().saveToLocal(getApplicationContext(), "province", UserData.address.getProvince());
                        new ImageProcessor().saveToLocal(getApplicationContext(), "zipcode", UserData.address.getZipcode()+"");

                        //Create avatar
                        Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                        UserData.photo = bitmap;
                        new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "avatar.dat");

                        downloadAdoption(uid);

                        //Send broadcast
                        Intent updateA = new Intent("update-avatar");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateA);

                        //Send broadcast
                        Intent updateN = new Intent("update-name");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateN);

                        //Send broadcast
                        Intent intent = new Intent("update-name");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        if (!UserData.accountStatus) {
                            //Notify User that the account is deactivated
                            Intent gotoDeact = new Intent(LoginLoadingScreen.this, DeactivatedScreen.class);
                            intent.putExtra("uid", UserData.userID);
                            startActivity(gotoDeact);
                            finish();
                        }

                        UserData.populate(LoginLoadingScreen.this);

                        //Once all data is retrieved, route user to Homepage
                        Intent gotoHPB = new Intent(LoginLoadingScreen.this, HorizontalProgressBar.class);
                        startActivity(gotoHPB);
                        finish();

                    }
                    catch (Exception e){
                        Log.d("DEBUGGER>>>", e.getMessage());
                        Intent i = new Intent(LoginLoadingScreen.this, Splash.class);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else{
            Toast.makeText(this, "Account can't be resolved. (LLS)", Toast.LENGTH_SHORT).show();
        }

    }

    private void downloadAdoption(String uid){
        //pending adoption
        DatabaseReference adoptionRef = database.getReference("adoptionRequest").child(uid);
        adoptionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUGGER>>>","downloading adoption...");
                try {
                    String dateRquested = snapshot.child("dateRequested").getValue().toString();
                    String petID = snapshot.child("petID").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();

                    UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "dateRequested", dateRquested);
                    UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "petID", petID);
                    UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "status", status);

                    try {
                        String appointmentDate = snapshot.child("appointmentDate").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "appointmentDate", appointmentDate);
                    }catch (Exception e){
                        Log.d("DEBUGGER>>>", "No appointment yet.");
                    }

                    try {
                        String dateReleased = snapshot.child("dateReleased").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "dateReleased", dateReleased);
                    }catch (Exception e){
                        Log.d("DEBUGGER>>>", "No dateReleased yet.");
                    }
                }
                catch (Exception e){
                    Log.d("DEBUGGER>>>", e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}