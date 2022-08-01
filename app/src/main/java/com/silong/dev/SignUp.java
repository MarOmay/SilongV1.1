package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.silong.Object.User;
import com.silong.Operation.InputValidator;

import java.io.Serializable;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ImageView backButton;
    static EditText fieldFname, fieldLname, fieldPassword, fieldConfirmpass,
            fieldEmail, fieldDBirthday, fieldContact;
    Spinner spinGender;
    Button next;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        //Initialize Firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        backButton = (ImageView) findViewById(R.id.btnBack);
        fieldDBirthday = (EditText) findViewById(R.id.tfsignupBirthday);
        next = (Button) findViewById(R.id.btnsignupNext);
        spinGender = findViewById(R.id.spsignupGender);
        fieldFname = (EditText) findViewById(R.id.tfsignupFname);
        fieldLname = (EditText) findViewById(R.id.tfsignupLname);
        fieldPassword = (EditText) findViewById(R.id.tfsignupPassword);
        fieldConfirmpass = (EditText) findViewById(R.id.tfsignupConPassword);
        fieldEmail = (EditText) findViewById(R.id.tfsignupEmail);
        fieldContact = (EditText) findViewById(R.id.tfsignupContact);

        String[] gen = getResources().getStringArray(R.array.Gender);

        fieldDBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_items, gen) {
            @Override
            public int getCount() {
                return gen.length-1;
            }
        };
        spinGender.setAdapter(genderAdapter);
        spinGender.setSelection(gen.length-1);

        //Check if there's data forwarded by other activity
        {
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

        //Intent to next screen of SignUP
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                String email = fieldEmail.getText().toString();
                if (!InputValidator.checkEmail(email)){
                    Toast.makeText(getApplicationContext(), "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check format of contact number
                String tempContact = fieldContact.getText().toString();
                if (!InputValidator.checkContact(tempContact)){
                    Toast.makeText(SignUp.this, "Please follow the number format: 09xxxxxxxxx", Toast.LENGTH_SHORT).show();
                    return;
                }

                user = new User();
                user.setFirstName(fieldFname.getText().toString());
                user.setLastName(fieldLname.getText().toString());
                user.setEmail(fieldEmail.getText().toString());
                user.setBirthday(fieldDBirthday.getText().toString());
                user.setGender(spinGender.getSelectedItem().toString().equals("Male")?0:1);
                user.setContact(fieldContact.getText().toString());

                emailChecker(getApplicationContext(), fieldEmail.getText().toString());

            }
        });

    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this, LogIn.class);
        startActivity(intent);
        this.finish();
    }

    private void emailChecker(Context context, String email){
        //Check internet connection
        if(internetConnection()){
            //Check if email is registered
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            try{
                                if (task.getResult().getSignInMethods().isEmpty()){
                                    Intent i = new Intent(SignUp.this, SignUp2.class);
                                    i.putExtra("DATA", user);
                                    i.putExtra("PASSWORD", fieldPassword.getText().toString());
                                    startActivity(i);
                                    finish();
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

    private boolean internetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return true;
        }
        return false;
    }

    //date picker fragment
    //only allows 18 years old and above
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            c.add(Calendar.YEAR, -18);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            fieldDBirthday.setText(month+1+"/"+day+"/"+year);
        }
    }
}