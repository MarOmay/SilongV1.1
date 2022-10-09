package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.ConfirmOTP;
import com.silong.CustomView.DatePickerFragment;
import com.silong.CustomView.GenderSpinner;
import com.silong.Object.User;
import com.silong.Operation.EmailOTP;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

import java.io.Serializable;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EmailOTP emailOTP;

    ImageView backButton, signUpPassHide, signUpConPassHide;
    static EditText fieldFname, fieldLname, fieldPassword, fieldConfirmpass,
            fieldEmail, fieldDBirthday, fieldContact;
    Spinner spinGender;
    Button next;
    int ctr = 0, ctr1 = 0;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        //Initialize Firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        //Receive date from DatePickerFragment
        LocalBroadcastManager.getInstance(this).registerReceiver(mDate, new IntentFilter("update-date"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mOTPReceiver, new IntentFilter("otp-submit"));

        backButton = (ImageView) findViewById(R.id.btnBack);
        fieldDBirthday = (EditText) findViewById(R.id.tfsignupBirthday);
        next = (Button) findViewById(R.id.btnsignupNext);
        spinGender = (GenderSpinner) findViewById(R.id.spsignupGender);
        fieldFname = (EditText) findViewById(R.id.tfsignupFname);
        fieldLname = (EditText) findViewById(R.id.tfsignupLname);
        fieldPassword = (EditText) findViewById(R.id.tfsignupPassword);
        fieldConfirmpass = (EditText) findViewById(R.id.tfsignupConPassword);
        fieldEmail = (EditText) findViewById(R.id.tfsignupEmail);
        fieldContact = (EditText) findViewById(R.id.tfsignupContact);
        signUpPassHide = (ImageView) findViewById(R.id.signUpPassHide);
        signUpConPassHide = (ImageView) findViewById(R.id.signUpConPassHide);

        //Check if there's data forwarded by other activity
        checkIntentForExtras();
    }

    public void checkIntentForExtras(){
        try {
            User temp = (User) getIntent().getSerializableExtra("SIGNUPDATA");
            fieldFname.setText(temp.getFirstName());
            fieldLname.setText(temp.getLastName());
            fieldEmail.setText(temp.getEmail());
            spinGender.setSelection(temp.getGender());
            fieldDBirthday.setText(temp.getBirthday());
            fieldContact.setText(temp.getContact());

            String password = getIntent().getStringExtra("PASSWORD");
            fieldPassword.setText(password);
            fieldConfirmpass.setText(password);
        }
        catch (Exception e){
            Log.d("SignUp", e.getMessage());
        }
    }

    public void onPressedBirthday(View view){
        DialogFragment newFragment = new DatePickerFragment(SignUp.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onPressedNext(View view){
        //Validate entries before accepting response
        if(fieldFname.getText().toString().trim().length() < 1||
                fieldLname.getText().toString().trim().length() < 1 ||
                fieldPassword.getText().toString().trim().length() < 1 ||
                fieldConfirmpass.getText().toString().trim().length() < 1 ||
                fieldEmail.getText().toString().trim().length() < 1 ||
                fieldDBirthday.getText().length() < 1 ||
                spinGender.getSelectedItem().toString().equals("Gender") ||
                fieldContact.getText().toString().trim().length() < 1
        ){
            Toast.makeText(getApplicationContext(), "Please answer all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (fieldPassword.getText().length() < 8){
            Toast.makeText(getApplicationContext(), "Password must be at least 8 characters.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!fieldPassword.getText().toString().equals(fieldConfirmpass.getText().toString())){
            Toast.makeText(getApplicationContext(), "Password does not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Check that name only includes special characters
        String fname = fieldFname.getText().toString().trim();
        if (!InputValidator.checkName(fname)) {
            Toast.makeText(getApplicationContext(), "Please check your first name.", Toast.LENGTH_SHORT).show();
            return;
        }

        String lname = fieldFname.getText().toString().trim();
        if (!InputValidator.checkName(lname)) {
            Toast.makeText(getApplicationContext(), "Please check your last name.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Checks format of the email
        String email = fieldEmail.getText().toString().trim();
        if (!InputValidator.checkEmail(email)){
            Toast.makeText(getApplicationContext(), "Please check the format of your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Check format of contact number
        String tempContact = fieldContact.getText().toString().trim();
        if (!InputValidator.checkContact(tempContact)){
            Toast.makeText(SignUp.this, "Please follow the number format: 09xxxxxxxxx", Toast.LENGTH_SHORT).show();
            return;
        }

        user = new User();
        user.setFirstName(fieldFname.getText().toString().trim());
        user.setLastName(fieldLname.getText().toString().trim());
        user.setEmail(fieldEmail.getText().toString().trim());
        user.setBirthday(fieldDBirthday.getText().toString());
        user.setGender(spinGender.getSelectedItem().toString().equals("Male")?0:1);
        user.setContact(fieldContact.getText().toString().trim());

        emailChecker(getApplicationContext(), fieldEmail.getText().toString().trim());
    }

    private void emailChecker(Context context, String email){
        //Check internet connection
        if(Utility.internetConnection(getApplicationContext())){
            //Check if email is registered
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            try{
                                if (task.getResult().getSignInMethods().isEmpty()){

                                    //send OTP
                                    emailOTP = new EmailOTP(SignUp.this, email);
                                    emailOTP.sendOTP();

                                    Utility.log("OTP: " + emailOTP.getOTP());

                                    //prompt otp
                                    ConfirmOTP confirmOTP = new ConfirmOTP(SignUp.this);
                                    confirmOTP.show();

                                }
                                else {
                                    //Inform user that email is in use
                                    Toast.makeText(getApplicationContext(), "Email is already registered.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                                Log.d("SignUp", e.getMessage());
                                return;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

    }

    private BroadcastReceiver mDate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fieldDBirthday.setText(intent.getStringExtra("date"));
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
                    //forward to SignUp2
                    Intent i = new Intent(SignUp.this, SignUp2.class);
                    i.putExtra("DATA", user);
                    i.putExtra("PASSWORD", fieldPassword.getText().toString());
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "OTP did not match", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error processing OTP", Toast.LENGTH_SHORT).show();
                Utility.log("SignUp.mOTPR: " + e.getMessage());
            }

        }
    };

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this, LogIn.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDate);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOTPReceiver);
    }

    public void onPassShowHide(View view){
        if (ctr == 0){
            new Utility().passwordFieldTransformer(fieldPassword, true);
            signUpPassHide.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_24));
            fieldPassword.setSelection(fieldPassword.getText().length());
            ctr++;
        }
        else {
            new Utility().passwordFieldTransformer(fieldPassword, false);
            signUpPassHide.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_off_24));
            fieldPassword.setSelection(fieldPassword.getText().length());
            ctr--;
        }
    }

    public void onConPassShowHide(View view){
        if (ctr == 0){
            new Utility().passwordFieldTransformer(fieldConfirmpass, true);
            signUpConPassHide.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_24));
            fieldConfirmpass.setSelection(fieldConfirmpass.getText().length());
            ctr++;
        }
        else {
            new Utility().passwordFieldTransformer(fieldConfirmpass, false);
            signUpConPassHide.setImageDrawable(getDrawable(R.drawable.ic_baseline_visibility_off_24));
            fieldConfirmpass.setSelection(fieldConfirmpass.getText().length());
            ctr--;
        }
    }
}