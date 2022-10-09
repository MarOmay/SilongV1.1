package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.AccountDeletionDialog;
import com.silong.CustomView.ConfirmOTP;
import com.silong.CustomView.EmailPrompt;
import com.silong.CustomView.PasswordPrompt;
import com.silong.CustomView.RequestDeactivationDialog;
import com.silong.CustomView.ResetLinkNotice;
import com.silong.Operation.EmailOTP;
import com.silong.Operation.Utility;

import java.util.Map;

public class AccountSecuritySettings extends AppCompatActivity {

    public static boolean FORBID_DEACTIVATION = false;

    private LinearLayout resetPasswordBtn, deactivateAccountBtn, deleteAccountBtn;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    private int afterPasswordOperation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security_settings);
        getSupportActionBar().hide();

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mEmailReceiver, new IntentFilter("reset-password-email"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mPasswordReceiver, new IntentFilter("password-submit"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeletion, new IntentFilter("delete-account-now"));

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();

        //initialize views
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        deactivateAccountBtn = findViewById(R.id.deactivateAccountBtn);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

    }

    public void onPressedResetPassword(View view){
        //check internet connection
        if (!Utility.internetConnection(AccountSecuritySettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        //Get email
        EmailPrompt emailPrompt = new EmailPrompt(AccountSecuritySettings.this, "Reset Password");
        emailPrompt.show();
    }

    public void onPressedDeactivate(View view){
        //check pending adoption
        if (FORBID_DEACTIVATION){
            Toast.makeText(this, "Pending adoption detected.", Toast.LENGTH_SHORT).show();
            return;
        }

        //check internet connection
        if (!Utility.internetConnection(AccountSecuritySettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            //prompt password OTP
            PasswordPrompt passwordPrompt = new PasswordPrompt(AccountSecuritySettings.this);
            passwordPrompt.show();

            afterPasswordOperation = 1;

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "OTP error", Toast.LENGTH_SHORT).show();
            Utility.log("ASS.oPR: " +  e.getMessage());
        }

    }

    public void onPressedDelete(View view){
        //check pending adoption
        if (FORBID_DEACTIVATION){
            Toast.makeText(this, "Pending adoption detected.", Toast.LENGTH_SHORT).show();
            return;
        }

        //check internet connection
        if (!Utility.internetConnection(AccountSecuritySettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            //prompt password OTP
            PasswordPrompt passwordPrompt = new PasswordPrompt(AccountSecuritySettings.this);
            passwordPrompt.show();

            afterPasswordOperation = 2;

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "OTP error", Toast.LENGTH_SHORT).show();
            Utility.log("ASS.oPR: " +  e.getMessage());
        }
    }

    private void deactivateAccount(boolean restart){
        //Check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            //disable account specific status
            mReference = mDatabase.getReference("Users/" + UserData.userID).child("accountStatus");
            mReference.setValue(false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //disable in summary
                            mReference = mDatabase.getReference("accountSummary").child(UserData.userID);
                            mReference.setValue(restart ? false : "deleted")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if (restart){
                                                Toast.makeText(AccountSecuritySettings.this, "Deactivating your account.", Toast.LENGTH_SHORT).show();
                                                restartApp();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AccountSecuritySettings.this, "Unable to complete request.", Toast.LENGTH_SHORT).show();
                                            Utility.log("ASS.dA: " + e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AccountSecuritySettings.this, "Unable to complete request.", Toast.LENGTH_SHORT).show();
                            Utility.log("ASS.dA: " + e.getMessage());
                        }
                    });
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartApp(){
        //Log out user
        UserData.logout(AccountSecuritySettings.this);
        mAuth.signOut();
        Intent i = new Intent(AccountSecuritySettings.this, Splash.class);
        startActivity(i);
        finish();
    }

    private void deleteAccount(){
        //Check internet connection
        if (Utility.internetConnection(getApplicationContext())){
            try {
                deactivateAccount(false);
                mAuth.getCurrentUser().delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                                restartApp();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Unable to complete request.", Toast.LENGTH_SHORT).show();
                                Utility.log("ASS.delA: " + e.getMessage());
                            }
                        });
            }
            catch (Exception e){
                Toast.makeText(this, "Unable to complete request.", Toast.LENGTH_SHORT).show();
                Utility.log("ASS.delA: " + e.getMessage());
            }
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }


    // BROADCAST RECEIVERS


    private BroadcastReceiver mEmailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                String email = intent.getStringExtra("email");

                FirebaseUser user = mAuth.getCurrentUser();

                if (!email.equals(user.getEmail())){
                    Toast.makeText(getApplicationContext(), "Email did not match.", Toast.LENGTH_SHORT).show();
                }
                else {

                    LoadingDialog loadingDialog = new LoadingDialog(AccountSecuritySettings.this);
                    loadingDialog.startLoadingDialog();

                    //Send a password reset link to email
                    mAuth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //Show email instruction dialog
                                    ResetLinkNotice resetLinkNotice = new ResetLinkNotice(AccountSecuritySettings.this);
                                    resetLinkNotice.show();
                                    loadingDialog.dismissLoadingDialog();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to process request", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissLoadingDialog();
                                }
                            });

                }


            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Email error", Toast.LENGTH_SHORT).show();
                Utility.log("ASS.mER: " +  e.getMessage());
            }

        }
    };

    private BroadcastReceiver mPasswordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LoadingDialog loadingDialog = new LoadingDialog(AccountSecuritySettings.this);
            loadingDialog.startLoadingDialog();

            try {

                String password = intent.getStringExtra("password");

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

                                    switch (afterPasswordOperation){
                                        case 0:
                                            Utility.log("CIS.mPR: No operation triggered");
                                            break;
                                        case 1:
                                            //show deactivation notice
                                            RequestDeactivationDialog rdd = new RequestDeactivationDialog(AccountSecuritySettings.this);
                                            rdd.show();
                                            break;
                                        case 2:
                                            //show deletion notice
                                            AccountDeletionDialog add = new AccountDeletionDialog(AccountSecuritySettings.this);
                                            add.show();
                                            break;
                                    }

                                    afterPasswordOperation = 0;

                                    loadingDialog.dismissLoadingDialog();

                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Failed to authenticate", Toast.LENGTH_SHORT).show();
                                    Utility.log("ASS.mPR: " + task.getException().getMessage());
                                    loadingDialog.dismissLoadingDialog();
                                }

                            }
                        });


            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Password error", Toast.LENGTH_SHORT).show();
                Utility.log("ASS.mPR: " + e.getMessage());
                loadingDialog.dismissLoadingDialog();
            }
        }
    };

    private BroadcastReceiver mDeactivation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deactivateAccount(true);
        }
    };

    private BroadcastReceiver mDeletion = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            deleteAccount();
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mEmailReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPasswordReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeletion);
        super.onDestroy();
    }
}