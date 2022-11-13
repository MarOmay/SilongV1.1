package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomView.BarangaySpinner;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;

import java.util.HashMap;
import java.util.Map;

public class AddressSettings extends AppCompatActivity {

    private EditText newAddressEt, newZipEt;
    private BarangaySpinner newBarangaySp;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_settings);
        getSupportActionBar().hide();

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mZipReceiver, new IntentFilter("barangay-zip"));

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //initialize views
        newAddressEt = findViewById(R.id.newAddressEt);
        newZipEt = findViewById(R.id.newZipEt);
        newBarangaySp = findViewById(R.id.newBarangaySp);

        //display user data
        newAddressEt.setText(UserData.address.getAddressLine());
        newZipEt.setText(String.valueOf(UserData.address.getZipcode()));
        newBarangaySp.setSelection(newBarangaySp.getPositionOf(UserData.address.getBarangay()));

    }

    public void onPressedSave(View view){
        //check internet connection
        if (!Utility.internetConnection(AddressSettings.this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            LoadingDialog loadingDialog = new LoadingDialog(AddressSettings.this);
            loadingDialog.startLoadingDialog();

            //extract input
            String addressLine = newAddressEt.getText().toString().trim().replace("\n", " ");
            String barangay = newBarangaySp.getSelectedItem().toString();
            int zipcode = Integer.parseInt(newBarangaySp.getZip());

            //validate input
            if (addressLine.length() <= 5){
                Toast.makeText(this, "Please provide a valid address", Toast.LENGTH_SHORT).show();
                return;
            }

            //prepare to upload changes
            Map<String, Object> map = new HashMap<>();

            if (!addressLine.equals(UserData.address.getAddressLine())){
                map.put("address/addressLine", addressLine);
            }
            if (!barangay.equals(UserData.address.getBarangay())){
                map.put("address/barangay", barangay);
                map.put("address/zipcode", zipcode);
            }

            if (map.isEmpty()){
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
                loadingDialog.dismissLoadingDialog();
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

                                //update local record
                                if (!addressLine.equals(UserData.address.getAddressLine())){
                                    UserData.address.setAddressLine(addressLine);
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "addressLine", addressLine);
                                }
                                if (!barangay.equals(UserData.address.getBarangay())){
                                    UserData.address.setBarangay(barangay);
                                    UserData.address.setZipcode(zipcode);
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "barangay", barangay);
                                    new ImageProcessor().saveToLocal(getApplicationContext(), "zipcode", String.valueOf(zipcode));
                                }

                                Toast.makeText(AddressSettings.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissLoadingDialog();

                                //go back to settings
                                onBackPressed();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(AddressSettings.this, "Failed to save changes.", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissLoadingDialog();

                            }
                        });

            }

        }
        catch (Exception e){
            Toast.makeText(this, "Can't save changes.", Toast.LENGTH_SHORT).show();
            Utility.log("AddressSettings.oPS: " + e.getMessage());
        }

    }

    public void onPressedDefault(View view){
        Toast.makeText(getApplicationContext(), "Silong is exclusive to San Jose del Monte City.", Toast.LENGTH_SHORT).show();
    }


    private BroadcastReceiver mZipReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                //update zipcode in ui
                String zipcode = intent.getStringExtra("zip");
                newZipEt.setText(zipcode);

            }
            catch (Exception e){
                Utility.log("AddressSettings.mZR: " + e.getMessage());
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mZipReceiver);
        super.onDestroy();
    }
}