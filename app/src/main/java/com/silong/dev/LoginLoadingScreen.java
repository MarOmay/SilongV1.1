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
            databaseReferenceUser.child("accountStatus").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        UserData.accountStatus = (Boolean) snapshot.getValue();
                        if (!UserData.accountStatus) {
                            //Notify User that the account is deactivated
                            Intent intent = new Intent(LoginLoadingScreen.this, DeactivatedScreen.class);
                            intent.putExtra("uid", UserData.userID);
                            startActivity(intent);
                            finish();
                        }
                        new ImageProcessor().saveToLocal(getApplicationContext(), "accountStatus", UserData.accountStatus?"true":"false");
                    }
                    catch (Exception e){
                        Log.d("LLS", e.getMessage());
                        Intent i = new Intent(LoginLoadingScreen.this, Splash.class);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //else, get all the other info
            databaseReferenceUser.child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.email = snapshot.getValue().toString();
                    new ImageProcessor().saveToLocal(getApplicationContext(), "email", UserData.email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("firstName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.firstName = snapshot.getValue().toString();
                    new ImageProcessor().saveToLocal(getApplicationContext(), "firstName", UserData.firstName);
                    //Send broadcast
                    Intent intent = new Intent("update-name");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    Toast.makeText(getApplicationContext(), "Welcome, " + UserData.firstName + "!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("lastName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.lastName = snapshot.getValue().toString();
                    new ImageProcessor().saveToLocal(getApplicationContext(), "lastName", UserData.lastName);
                    //Send broadcast
                    Intent intent = new Intent("update-name");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("birthday").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.birthday = snapshot.getValue().toString();
                    new ImageProcessor().saveToLocal(getApplicationContext(), "birthday", UserData.birthday);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("gender").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.gender = Integer.parseInt(snapshot.getValue().toString());
                    new ImageProcessor().saveToLocal(getApplicationContext(), "gender", UserData.gender+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("contact").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.contact = snapshot.getValue().toString();
                    new ImageProcessor().saveToLocal(getApplicationContext(), "contact", UserData.contact);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("photo").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String photo = snapshot.getValue().toString();
                    //Create avatar
                    Bitmap bitmap = new ImageProcessor().toBitmap(photo);
                    UserData.photo = bitmap;
                    new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "avatar.dat");
                    //Send broadcast
                    Intent intent = new Intent("update-avatar");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("adoptionCounter").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.adoptionCounter = Integer.parseInt(snapshot.getValue().toString());
                    new ImageProcessor().saveToLocal(getApplicationContext(), "adoptionCounter", UserData.adoptionCounter+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            databaseReferenceUser.child("address").child("addressLine").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.address.setAddressLine(snapshot.getValue().toString());
                    new ImageProcessor().saveToLocal(getApplicationContext(), "addressLine", UserData.address.getAddressLine());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("address").child("barangay").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.address.setBarangay(snapshot.getValue().toString());
                    new ImageProcessor().saveToLocal(getApplicationContext(), "barangay", UserData.address.getBarangay());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("address").child("municipality").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.address.setMunicipality(snapshot.getValue().toString());
                    new ImageProcessor().saveToLocal(getApplicationContext(), "municipality", UserData.address.getMunicipality());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("address").child("province").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.address.setProvince(snapshot.getValue().toString());
                    new ImageProcessor().saveToLocal(getApplicationContext(), "province", UserData.address.getProvince());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceUser.child("address").child("zipcode").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData.address.setZipcode(Integer.parseInt(snapshot.getValue().toString()));
                    new ImageProcessor().saveToLocal(getApplicationContext(), "zipcode", UserData.address.getZipcode()+"");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //pending adoption
            DatabaseReference adoptionRef = database.getReference().child("adoptionRequest").child(uid);
            adoptionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String dateRquested = snapshot.child("dateRequested").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "dateRequested", dateRquested);

                        String petID = snapshot.child("petID").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "petID", petID);

                        String status = snapshot.child("status").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "status", status);

                        String appointmentDate = snapshot.child("appointmentDate").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "appointmentDate", appointmentDate);

                        String dateReleased = snapshot.child("dateReleased").getValue().toString();
                        UserData.writeAdoptionToLocal(LoginLoadingScreen.this, dateRquested, "dateReleased", dateReleased);
                    }
                    catch (Exception e){
                        Log.d("DEBUGGER>>>", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //insert chat history
            //insert liked pet

            //Once all data is retrieved, route user to Homepage
            Intent intent = new Intent(LoginLoadingScreen.this, HorizontalProgressBar.class);
            startActivity(intent);
            finish();

        }
        else{
            Toast.makeText(this, "Account can't be resolved. (LLS)", Toast.LENGTH_SHORT).show();
        }

    }
}