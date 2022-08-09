package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.silong.CustomView.RequestDeactivationDialog;
import com.silong.CustomView.AccountDeletionDialog;

public class EditProfile extends AppCompatActivity {

    EditText newGenderEt, newBirthdayEt, newContactEt;
    TextView deleteTv, deactTv;
    Button saveChangesBtn;
    Spinner newBarangaySp;
    ImageView newPictureIv;
    boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        newBirthdayEt = (EditText) findViewById(R.id.newBirthdayEt);
        newContactEt = (EditText) findViewById(R.id.newContactEt);
        newGenderEt = (EditText) findViewById(R.id.newGenderEt);
        deleteTv = (TextView) findViewById(R.id.deleteTv);
        deactTv = (TextView) findViewById(R.id.deactTv);
        newPictureIv = (ImageView) findViewById(R.id.newPictureIv);
        saveChangesBtn = (Button) findViewById(R.id.saveChangesBtn);
        newBarangaySp = (Spinner) findViewById(R.id.newBarangaySp);
    }

    public void onRequestDeactivationPressed(View view){
        RequestDeactivationDialog requestDeactivationDialog = new RequestDeactivationDialog(EditProfile.this);
        requestDeactivationDialog.show();
    }

    public void onRequestDeletionPressed(View view){
        AccountDeletionDialog requestDeletionDialog = new AccountDeletionDialog(EditProfile.this);
        requestDeletionDialog.show();
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