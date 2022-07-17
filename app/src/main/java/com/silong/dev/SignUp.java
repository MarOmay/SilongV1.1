package com.silong.dev;

import static com.silong.dev.LogIn.setWindowFlag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Object.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

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

        //FOR TESTING PURPOSES
        fieldFname.setText("John");
        fieldLname.setText("Smith");
        fieldPassword.setText("password");
        fieldConfirmpass.setText("password");
        fieldEmail.setText("johnsmith@gmail.com");
        fieldContact.setText("1");
        //END OF TESTING BLOCK

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

        //Intent to next screen of SignUP
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validate entries before accepting response
                if(fieldFname.getText().equals("") ||
                        fieldLname.getText().equals("") ||
                        fieldPassword.getText().equals("") ||
                        fieldConfirmpass.getText().equals("") ||
                        fieldEmail.getText().equals("") ||
                        fieldDBirthday.getText().equals(null) ||
                        spinGender.getSelectedItem().toString().equals("Gender") ||
                        fieldContact.getText().equals("")
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

                //Checks format of the email
                String email = fieldEmail.getText().toString();
                Pattern pattern = Pattern.compile("^(.+)@(.+)$");
                Matcher matcher = pattern.matcher(fieldEmail.getText().toString());
                if (!matcher.matches()){
                    Toast.makeText(getApplicationContext(), "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();
                user.setFirstName(fieldFname.getText().toString());
                user.setLastName(fieldLname.getText().toString());
                user.setEmail(fieldEmail.getText().toString());
                user.setBirthday(fieldDBirthday.getText().toString());
                user.setGender(spinGender.getSelectedItem().toString().equals("Male")?0:1);
                user.setContact(fieldContact.getText().toString());

                Intent i = new Intent(SignUp.this, SignUp2.class);
                i.putExtra("DATA", user);
                i.putExtra("PASSWORD", fieldPassword.getText().toString());
                startActivity(i);


            }
        });

    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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