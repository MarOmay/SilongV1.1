package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.BarangaySpinner;
import com.silong.CustomView.RequestDeactivationDialog;
import com.silong.CustomView.AccountDeletionDialog;
import com.silong.Operation.ImagePicker;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.InputValidator;
import com.silong.Operation.Utility;

import java.io.BufferedInputStream;

public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    EditText newGenderEt, newAddressEt, newBirthdayEt, newContactEt;
    BarangaySpinner barangaySpinner;
    Button saveChangesBtn;
    ImageView newPictureIv;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        //Initialize Firebase Objects
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Register broadcast receivers
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeactivation, new IntentFilter("deactivate-account-now"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeletion, new IntentFilter("delete-account-now"));

        newBirthdayEt = (EditText) findViewById(R.id.newBirthdayEt);
        newGenderEt = (EditText) findViewById(R.id.newGenderEt);
        newContactEt = (EditText) findViewById(R.id.newContactEt);
        newPictureIv = (ImageView) findViewById(R.id.newPictureIv);
        newAddressEt = (EditText) findViewById(R.id.newAddressEt);
        barangaySpinner = (BarangaySpinner) findViewById(R.id.newBarangaySp);
        saveChangesBtn = (Button) findViewById(R.id.saveChangesBtn);

        newBirthdayEt.setText(UserData.birthday);
        newGenderEt.setText(UserData.gender == 0 ? "Male" : "Female");
        newContactEt.setText(UserData.contact);
        newPictureIv.setImageBitmap(UserData.photo);
        newAddressEt.setText(UserData.address.getAddressLine());
        barangaySpinner.setSelection(barangaySpinner.getPositionOf(UserData.address.getBarangay()));
    }

    public void onPressedPhoto(View view){
        new ImagePicker(EditProfile.this, PICK_IMAGE);
    }

    public void onPressedFixed(View view){
        Toast.makeText(this, "This section can't be changed.", Toast.LENGTH_SHORT).show();
    }

    public void onPressedRequestDeactivation(View view){
        RequestDeactivationDialog requestDeactivationDialog = new RequestDeactivationDialog(EditProfile.this);
        requestDeactivationDialog.show();
    }

    public void onPressedRequestDeletion(View view){
        AccountDeletionDialog requestDeletionDialog = new AccountDeletionDialog(EditProfile.this);
        requestDeletionDialog.show();
    }

    private int savingProgress = 0;
    public void onPressedSave(View view){
        LoadingDialog loadingDialog = new LoadingDialog(EditProfile.this);
        loadingDialog.startLoadingDialog();

        boolean pendingChanges = false;

        Bitmap bitmap = ((BitmapDrawable)newPictureIv.getDrawable()).getBitmap();
        String addressLine = newAddressEt.getText().toString().trim();
        String barangay = barangaySpinner.getSelectedItem().toString();
        String contact = newContactEt.getText().toString().trim();

        //Validate inputs first
        if (!InputValidator.checkContact(contact)){
            Toast.makeText(this, "Please follow the number format: 09xxxxxxxxx", Toast.LENGTH_SHORT).show();
            return;
        }
        if (addressLine.length() < 5){
            Toast.makeText(this, "Address can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        //begin saving changes
        if (!bitmap.sameAs(UserData.photo)){
            pendingChanges = true;
            updatePhoto(new ImageProcessor().toUTF8(bitmap, true), bitmap);
        }
        else {
            savingProgress += 25;
            Log.d("EP", "photo +25");
        }

        if (!addressLine.equals(UserData.address.getAddressLine())){
            pendingChanges = true;
            updateAddress("addressLine", addressLine);
        }else {
            savingProgress += 25;
            Log.d("EP", "line +25");
        }

        if (!barangay.equals(UserData.address.getBarangay())){
            pendingChanges = true;
            updateAddress("barangay", barangay);
            updateAddress("zipcode", barangaySpinner.getZip());
        }else {
            savingProgress += 25;
            Log.d("EP", "brgy +25");
        }

        if (!contact.equals(UserData.contact)){
            pendingChanges = true;
            updateContact(contact);
        }else {
            savingProgress += 25;
            Log.d("EP", "contact +25");
        }

        if (pendingChanges){
            Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show();
            pendingChanges = false;
            loadingDialog.dismissLoadingDialog();
        }
        else {
            Toast.makeText(this, "No changes to be saved.", Toast.LENGTH_SHORT).show();
            loadingDialog.dismissLoadingDialog();
        }
    }

    private void checkProgress(){
        if (savingProgress == 100){
            Toast.makeText(EditProfile.this, "Changes saved!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void updatePhoto(String data, Bitmap bitmap){
        mReference = mDatabase.getReference("Users").child(UserData.userID).child("photo");
        mReference.setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        UserData.photo = bitmap;
                        new ImageProcessor().saveToLocal(getApplicationContext(), bitmap, "avatar.dat");
                        Intent intent = new Intent("update-avatar");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        savingProgress += 25;
                        checkProgress();
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    private void updateAddress(String node, String data){
        mReference = mDatabase.getReference("Users").child(UserData.userID).child("address").child(node);
        mReference.setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        switch (node){
                            case "addressLine" : UserData.address.setAddressLine(data); savingProgress += 25; break;
                            case "barangay" : UserData.address.setBarangay(data); savingProgress += 25; break;
                            case "zipcode" : UserData.address.setZipcode(Integer.parseInt(data)); break;
                        }
                        new ImageProcessor().saveToLocal(getApplicationContext(), node, data);
                        checkProgress();
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    private void updateContact(String data){
        mReference = mDatabase.getReference("Users").child(UserData.userID).child("contact");
        mReference.setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        UserData.contact = data;
                        new ImageProcessor().saveToLocal(getApplicationContext(), "contact", data);
                        savingProgress += 25;
                        checkProgress();
                    }
                })
                .addOnFailureListener(onFailureListener);
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
                            mReference.setValue(restart ? false : null)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            if (restart){
                                                Toast.makeText(EditProfile.this, "Deactivating your account.", Toast.LENGTH_SHORT).show();
                                                restartApp();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EditProfile.this, "Unable to complete request.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, "Unable to complete request.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
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
                            }
                        });
            }
            catch (Exception e){
                Toast.makeText(this, "Unable to complete request.", Toast.LENGTH_SHORT).show();
                Log.d("EditProfile", e.getMessage());
            }
        }
        else {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartApp(){
        //Log out user
        UserData.logout();
        mAuth.signOut();
        Intent i = new Intent(EditProfile.this, Splash.class);
        startActivity(i);
        finish();
    }

    private OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (Utility.internetConnection(getApplicationContext())){
                Toast.makeText(EditProfile.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(EditProfile.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
            }
            onBackPressed();
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
                newPictureIv.setImageBitmap(bitmap);
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
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeactivation);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeletion);
    }
}