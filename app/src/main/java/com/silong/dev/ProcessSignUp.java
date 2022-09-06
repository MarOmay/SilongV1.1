package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Object.Address;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProcessSignUp extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private User USER;
    private String PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_sign_up);
        getSupportActionBar().hide();

        //Initialize Firebase objects
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        //mReference = mDatabase.getReference("Users");

        USER = (User) getIntent().getSerializableExtra("DATA");
        PASSWORD = (String) getIntent().getStringExtra("PASSWORD");

        USER.setPhotoAsString(new ImageProcessor().toUTF8(UserData.photo, true));

        try{
            registerEmail();
        }
        catch (Exception e){
            Toast.makeText(ProcessSignUp.this, "There is a problem registering your email.", Toast.LENGTH_SHORT).show();
            Log.d("ProcessSignUp", e.getMessage());
            backToSignUp();
        }
    }

    private void registerEmail(){
        try {
            mAuth.createUserWithEmailAndPassword(USER.getEmail(), PASSWORD)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try{
                                    //Sign into account first for auth rules
                                    mAuth.signInWithEmailAndPassword(USER.getEmail(), PASSWORD)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    //Get uid from Firebase
                                                    USER.setUserID(mAuth.getCurrentUser().getUid());
                                                    saveUserData();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProcessSignUp.this, "Database error. PSU", Toast.LENGTH_SHORT).show();
                                                    Log.d("ProcessSignUp", e.getMessage());
                                                    //Bring user back to sign up page, and autofill the data
                                                    Intent intent = new Intent(ProcessSignUp.this, SignUp.class);
                                                    intent.putExtra("SIGNUPDATA", USER);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                                catch (Exception e){
                                    Toast.makeText(ProcessSignUp.this, "There is a problem getting you signed up.", Toast.LENGTH_SHORT).show();
                                    Log.d("ProcessSignUp", e.getMessage());
                                    backToSignUp();
                                }

                            }
                            else {
                                backToSignUp();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (!Utility.internetConnection(getApplicationContext())){
                                Toast.makeText(ProcessSignUp.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                            else if (e instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(ProcessSignUp.this, "Please use a valid email address.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ProcessSignUp.this, "There is a problem getting you signed up.", Toast.LENGTH_SHORT).show();
                            }

                            //Bring user back to sign up page, and autofill the data
                            backToSignUp();
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong. (PSU)", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void saveUserData(){
        mReference = mDatabase.getReference("Users/" + USER.getUserID());

        Map<String, Object> map = new HashMap<>();
        map.put("firstName",USER.getFirstName());
        map.put("lastName",USER.getLastName());
        map.put("email", USER.getEmail());
        map.put("birthday", USER.getBirthday());
        map.put("gender", USER.getGender());
        map.put("contact", USER.contact);
        map.put("photo", USER.getPhotoAsString());
        map.put("accountStatus", true);
        Address address = USER.getAddress();
        map.put("address/addressLine", address.getAddressLine());
        map.put("address/barangay", address.getBarangay());
        map.put("address/municipality", address.getMunicipality());
        map.put("address/province", address.getProvince());
        map.put("address/zipcode", address.getZipcode());
        map.put("chatHistory", 0);
        map.put("created", Utility.dateToday());

        mReference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut();
                        Toast.makeText(ProcessSignUp.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                        mReference = mDatabase.getReference("accountSummary/" + USER.getUserID());
                        mReference.setValue(true);

                        Intent intent = new Intent(ProcessSignUp.this, LogIn.class);
                        intent.putExtra("email", USER.getEmail());
                        intent.putExtra("password", PASSWORD);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (Utility.internetConnection(getApplicationContext())){
                            Toast.makeText(ProcessSignUp.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ProcessSignUp.this, "There's a problem getting you signed up.", Toast.LENGTH_SHORT).show();
                        }
                        onBackPressed();
                    }
                });
    }

    private void backToSignUp(){
        Intent intent = new Intent(ProcessSignUp.this, SignUp.class);
        intent.putExtra("SIGNUPDATA", USER);
        startActivity(intent);
        finish();
    }
}