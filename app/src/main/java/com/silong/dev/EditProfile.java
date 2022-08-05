package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.silong.CustomView.RequestDeactivationDialog;

public class EditProfile extends AppCompatActivity {

    EditText newPasswordEt, newFnameEt, newLnameEt, newBirthdayEt, newContactEt;
    Spinner newGenderSp;
    TextView requestResetTv, requestDeactTv;
    Button saveChangesBtn;
    ImageView newPictureIv;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        newFnameEt = (EditText) findViewById(R.id.newFnameEt);
        newLnameEt = (EditText) findViewById(R.id.newLnameEt);
        newPasswordEt = (EditText) findViewById(R.id.newPasswordEt);
        newBirthdayEt = (EditText) findViewById(R.id.newBirthdayEt);
        newContactEt = (EditText) findViewById(R.id.newContactEt);
        newGenderSp = (Spinner) findViewById(R.id.newGenderSp);
        requestResetTv = (TextView) findViewById(R.id.requestResetTv);
        requestDeactTv = (TextView) findViewById(R.id.requestDeactTv);
        newPictureIv = (ImageView) findViewById(R.id.newPictureIv);
        saveChangesBtn = (Button) findViewById(R.id.saveChangesBtn);
    }

    public void onRequestDeactivationPressed(View view){
        RequestDeactivationDialog requestDeactivationDialog = new RequestDeactivationDialog(EditProfile.this);
        requestDeactivationDialog.show();
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}