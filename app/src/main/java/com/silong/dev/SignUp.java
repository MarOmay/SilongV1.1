package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class SignUp extends AppCompatActivity {

    ImageView backButton;
    EditText fieldFname, fieldLname, fieldPassword, fieldConfirmpass,
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

        String[] gen = getResources().getStringArray(R.array.Gender);

        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

        fieldDBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getSupportFragmentManager(), "Material_Date_Picker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        fieldDBirthday.setText(datePicker.getHeaderText());
                    }
                });
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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp.this, SignUp2.class);
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
}