package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.ConfirmOTP;
import com.silong.CustomView.PasswordPrompt;
import com.silong.Operation.EmailOTP;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ContactInformationSettings extends AppCompatActivity {

    private EditText newContactEt, newEmailEt;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    private EmailOTP emailOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information_settings);
        getSupportActionBar().hide();

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mOTPReceiver, new IntentFilter("otp-submit"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalReceiver, new IntentFilter("cis-post"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mPasswordReceiver, new IntentFilter("password-submit"));

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();

        //initialize views
        newContactEt = findViewById(R.id.newContactEt);
        newEmailEt = findViewById(R.id.newEmailEt);

        //display user info
        newContactEt.setText(UserData.contact);
        newEmailEt.setText(UserData.email);

        //hide email
        new Utility().passwordFieldTransformer(newEmailEt, false);

    }

    public void onPressedSave(View view){
        //check internet connection
        if (!Utility.internetConnection(ContactInformationSettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            LoadingDialog loadingDialog = new LoadingDialog(ContactInformationSettings.this);
            loadingDialog.startLoadingDialog();

            String contact = newContactEt.getText().toString();
            String email = newEmailEt.getText().toString();

            Map<String, Object> map = new HashMap<>();

            if (!UserData.contact.equals(contact)){
                //check contact number format
                if (!InputValidator.checkContact(contact)){
                    Toast.makeText(ContactInformationSettings.this, "Please follow the number format: 09xxxxxxxxx", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissLoadingDialog();
                    return;
                }
                else {
                    //add to map
                    map.put("contact", contact);
                }
            }

            boolean withEmail = false;
            if (!UserData.email.equals(email)){
                if (!InputValidator.checkEmail(email)){
                    Toast.makeText(ContactInformationSettings.this, "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissLoadingDialog();
                    return;
                }
                else {
                    //add to map
                    map.put("email", email);
                    withEmail = true;
                }
            }

            if (map.isEmpty()){
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismissLoadingDialog();
            }
            else {

                if (withEmail){

                    //check first if email is already registered
                    mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            try {
                                if (task.getResult().getSignInMethods().isEmpty()){
                                    //upload changes
                                    Intent i = new Intent("cis-post");
                                    i.putExtra("map", (Serializable) map);
                                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                                    loadingDialog.dismissLoadingDialog();
                                }
                                else {
                                    Toast.makeText(ContactInformationSettings.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissLoadingDialog();
                                    return;
                                }
                            }
                            catch (Exception ex){
                                Toast.makeText(ContactInformationSettings.this, "Request failed", Toast.LENGTH_SHORT).show();
                                Utility.log("CIS.oPS: " + ex.getMessage());
                            }

                        }
                    });

                }
                else {
                    //upload changes right away
                    uploadChanges(map);
                    loadingDialog.dismissLoadingDialog();
                }
            }

        }
        catch (Exception e){
            Toast.makeText(ContactInformationSettings.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
            Utility.log("PIS.oPS: " + e.getMessage());
        }
    }

    public void onPressedEmail(View view){
        //check internet connection
        if (!Utility.internetConnection(ContactInformationSettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            //send OTP
            emailOTP = new EmailOTP(ContactInformationSettings.this, UserData.email);
            emailOTP.sendOTP();

            Utility.log("OTP: " + emailOTP.getOTP());

            //prompt otp
            ConfirmOTP confirmOTP = new ConfirmOTP(ContactInformationSettings.this);
            confirmOTP.show();

        }
        catch (Exception e){
            Toast.makeText(ContactInformationSettings.this, "Failed to confirm OTP.", Toast.LENGTH_SHORT).show();
            Utility.log("CSI.oPE: " + e.getMessage());
        }

    }

    private void uploadChanges(Map<String, Object> map){

        LoadingDialog loadingDialog = new LoadingDialog(ContactInformationSettings.this);
        loadingDialog.startLoadingDialog();

        try {

            //update lastModified
            map.put("lastModified", Utility.dateToday() + " " + Utility.timeNow());

            //update changes to rtdb
            mReference = mDatabase.getReference("Users").child(UserData.userID);
            mReference.updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            //save to local
                            if (map.containsKey("contact")){
                                String contact = (String) map.get("contact");
                                UserData.contact = contact;
                                new ImageProcessor().saveToLocal(getApplicationContext(), "contact", contact);
                            }
                            if (map.containsKey("email")){
                                String email = (String) map.get("email");
                                UserData.email = email;
                                new ImageProcessor().saveToLocal(getApplicationContext(), "email", email);
                            }

                            Toast.makeText(getApplicationContext(), "Changes saved.", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissLoadingDialog();
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to save changes.", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissLoadingDialog();
                            Utility.log("PIS.oPS-aOFL: " + e.getMessage());
                        }
                    });

        }
        catch (Exception e){
            Utility.log("CIS.uC: " + e.getMessage());
        }

    }

    private BroadcastReceiver mLocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Map<String, Object> map = (Map<String, Object>) intent.getSerializableExtra("map");

                PasswordPrompt passwordPrompt = new PasswordPrompt(ContactInformationSettings.this);
                passwordPrompt.setMap(map);
                passwordPrompt.show();

            }
            catch (Exception e){
                Utility.log("CIS.mLR: " + e.getMessage());
            }

        }
    };

    private BroadcastReceiver mOTPReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                //get otp from user input
                String tempOTP = intent.getStringExtra("otp");
                if (tempOTP.length() <= 0){
                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
                else if (emailOTP.getOTP().equals(tempOTP)){
                    //enable edittext
                    new Utility().passwordFieldTransformer(newEmailEt, true);
                    newEmailEt.setOnClickListener(null);
                    newEmailEt.setFocusableInTouchMode(true);
                    newEmailEt.setFocusable(true);
                    newEmailEt.requestFocus();
                }
                else {
                    Toast.makeText(getApplicationContext(), "OTP did not match", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error processing OTP", Toast.LENGTH_SHORT).show();
                Utility.log("CIS.mOTPR: " + e.getMessage());
            }

        }
    };

    private BroadcastReceiver mPasswordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LoadingDialog loadingDialog = new LoadingDialog(ContactInformationSettings.this);
            loadingDialog.startLoadingDialog();

            try {

                String password = intent.getStringExtra("password");
                Map<String, Object> map = (Map<String, Object>) intent.getSerializableExtra("map");

                if (password.length() <= 0){
                    Toast.makeText(getApplicationContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissLoadingDialog();
                    return;
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    //update email in FirebaseAuth
                                    user.updateEmail(map.get("email").toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    loadingDialog.dismissLoadingDialog();

                                                    if (task.isSuccessful()){
                                                        //upload changes to rtdb
                                                        uploadChanges(map);
                                                    }
                                                    else {
                                                        Toast.makeText(getApplicationContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                                        Utility.log("CIS.mLR: " + task.getException().getMessage());
                                                    }
                                                }
                                            });

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Failed to authenticate", Toast.LENGTH_SHORT).show();
                                    Utility.log("CIS.mPR: " + task.getException().getMessage());
                                    loadingDialog.dismissLoadingDialog();
                                }

                            }
                        });


            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Password error", Toast.LENGTH_SHORT).show();
                Utility.log("CIS.mPR: " + e.getMessage());
                loadingDialog.dismissLoadingDialog();
            }
        }
    };

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOTPReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocalReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPasswordReceiver);
        super.onDestroy();
    }
}