package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.DatePickerFragment;
import com.silong.CustomView.GenderSpinner;
import com.silong.Operation.ImagePicker;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.validation.Validator;

public class PersonalInformationSettings extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private ImageView newPictureIv;
    private EditText newFnameEt, newLnameEt, newBirthdayEt;
    private GenderSpinner newGenderEt;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information_settings);
        getSupportActionBar().hide();

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBDayReceiver, new IntentFilter("update-date"));

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //initialize views
        newPictureIv = findViewById(R.id.newPictureIv);
        newFnameEt = findViewById(R.id.newFnameEt);
        newLnameEt = findViewById(R.id.newLnameEt);
        newGenderEt = findViewById(R.id.newGenderEt);
        newBirthdayEt = findViewById(R.id.newBirthdayEt);

        //display user info
        newPictureIv.setImageBitmap(UserData.photo);
        newFnameEt.setText(UserData.firstName);
        newLnameEt.setText(UserData.lastName);
        newGenderEt.setSelection(newGenderEt.getArrayAdapter().getPosition(UserData.gender == 0 ? "Male" : "Female"));
        newBirthdayEt.setText(UserData.birthday);

    }

    public void onPressedAvatar(View view){
        new ImagePicker(PersonalInformationSettings.this, PICK_IMAGE);
    }

    public void onPressedBirthday(View view){
        DialogFragment newFragment = new DatePickerFragment(PersonalInformationSettings.this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onPressedSave(View view){
        //check internet connection
        if (!Utility.internetConnection(PersonalInformationSettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            LoadingDialog loadingDialog = new LoadingDialog(PersonalInformationSettings.this);
            loadingDialog.startLoadingDialog();

            //get displayed info
            Bitmap bitmap = ((BitmapDrawable)newPictureIv.getDrawable()).getBitmap();
            String firstname = newFnameEt.getText().toString();
            String lastname = newLnameEt.getText().toString();
            int gender = newGenderEt.getSelectedItem().toString().equals("Male") ? 0 : 1;
            String birthday = newBirthdayEt.getText().toString();

            //check validity of input
            if (!InputValidator.checkName(firstname) || !InputValidator.checkName(lastname)){
                Toast.makeText(this, "Please use a valid name", Toast.LENGTH_SHORT).show();
                return;
            }

            //prepare data
            Map<String, Object> map = new HashMap<>();
            if (!UserData.firstName.equals(firstname))
                map.put("firstName", firstname);
            if (!UserData.lastName.equals(lastname))
                map.put("lastName", lastname);
            if (UserData.gender != gender)
                map.put("gender", gender);
            if (!UserData.birthday.equals(birthday))
                map.put("birthday", birthday);
            if (!bitmap.sameAs(UserData.photo))
                map.put("photo", new ImageProcessor().toUTF8(bitmap, true));

            //check if there is something to save
            if (map.isEmpty()){
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
            }
            else {

                //update lastModified
                map.put("lastModified", Utility.dateToday() + " " + Utility.timeNow());

                //upload changes to rtdb
                mReference = mDatabase.getReference("Users").child(UserData.userID);
                mReference.updateChildren(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                //update static variables and local data
                                if (!UserData.firstName.equals(firstname)){
                                    UserData.firstName = firstname;
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "firstName", firstname);
                                }
                                if (!UserData.lastName.equals(lastname)){
                                    UserData.lastName = lastname;
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "lastName", lastname);
                                }
                                if (UserData.gender != gender){
                                    UserData.gender = gender;
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "gender", String.valueOf(gender));
                                }
                                if (!UserData.birthday.equals(birthday)){
                                    UserData.birthday = birthday;
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "birthday", birthday);
                                }
                                if (!bitmap.sameAs(UserData.photo)){
                                    UserData.photo = bitmap;
                                    new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "avatar.dat");
                                }

                                Toast.makeText(PersonalInformationSettings.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissLoadingDialog();

                                //go back to settings
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(PersonalInformationSettings.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissLoadingDialog();

                            }
                        });

            }

        }
        catch (Exception e){
            Toast.makeText(PersonalInformationSettings.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
            Utility.log("PIS.oPB: " + e.getMessage());
        }

    }

    private BroadcastReceiver mBDayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            newBirthdayEt.setText(intent.getStringExtra("date"));
        }
    };

    //overriding methods

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            try{
                BufferedInputStream bufferedInputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                if (!new ImageProcessor().checkFileSize(bitmap, true)) {
                    Toast.makeText(getApplicationContext(), "Please select a 1x1 picture less than 1MB.", Toast.LENGTH_SHORT).show();
                    return;
                }

                bitmap = new ImageProcessor().tempCompress(bitmap);

                try {
                    newPictureIv.setImageBitmap(bitmap);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Please select a picture less than 1MB.", Toast.LENGTH_SHORT).show();
                }

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to choose file", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBDayReceiver);
        super.onDestroy();
    }
}