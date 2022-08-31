package com.silong.dev;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.silong.CustomView.BarangaySpinner;
import com.silong.CustomView.TnCDialog;
import com.silong.Object.Address;
import com.silong.Object.User;
import com.silong.Operation.ImagePicker;
import com.silong.Operation.ImageProcessor;

import java.io.BufferedInputStream;
import java.util.Arrays;

public class SignUp2 extends AppCompatActivity {

    Button btnCreate;
    ImageView ivPicture;
    EditText etAddress;
    Spinner spinBarangay;
    EditText spinMunicipality;
    EditText spinProvince;
    EditText etZip;

    private static final int PICK_IMAGE = 1;
    private Bitmap bmp;

    private User user;
    private String password;
    private byte [] photoAsBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        getSupportActionBar().hide();

        user = (User) getIntent().getSerializableExtra("DATA");
        password = (String) getIntent().getExtras().getString("PASSWORD");

        //Positive Button from TnC Dialog
        LocalBroadcastManager.getInstance(this).registerReceiver(mPositive, new IntentFilter("tnc-accepted"));

        //Receive zipcode from BarangaySpinner
        LocalBroadcastManager.getInstance(this).registerReceiver(mZip, new IntentFilter("barangay-zip"));

        btnCreate = (Button) findViewById(R.id.btnsignupCreate);
        ivPicture = (ImageView) findViewById(R.id.ivsignupPicture);
        etAddress = (EditText) findViewById(R.id.tfsignupAddress);
        spinBarangay = (BarangaySpinner) findViewById(R.id.spsignupBarangay);
        spinMunicipality = (EditText) findViewById(R.id.spsignupMunicipality);
        spinProvince = (EditText) findViewById(R.id.spsignupProvince);
        etZip = (EditText) findViewById(R.id.tfsignupZip);

        spinMunicipality.setText("City of San Jose del Monte");
        spinProvince.setText("Bulacan");
        etZip.setText("3023");

    }

    public void onPressedAvatar(View view){
        new ImagePicker (SignUp2.this, PICK_IMAGE);
    }

    public void onPressedDefault(View view){
        Toast.makeText(getApplicationContext(), "Silong is exclusive to San Jose del Monte City.", Toast.LENGTH_SHORT).show();
    }

    public void onPressedCreate(View view){
        //Validate entries before accepting response
        if (etAddress.getText().toString().trim().length() < 1 ||
                spinBarangay.getSelectedItem().equals("Barangay")
        ){
            Toast.makeText(getApplicationContext(), "Please answer all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ivPicture.getDrawable() == null){
            Toast.makeText(getApplicationContext(), "Please select a 1x1 photo.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (new ImageProcessor().checkFileSize(ivPicture.getDrawable(), true) == false){
            Toast.makeText(getApplicationContext(), "Please select a 1x1 picture less than 1MB.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Add additional input to User object
        Address address = new Address(etAddress.getText().toString(),
                spinBarangay.getSelectedItem().toString(),
                spinMunicipality.getText().toString(),
                spinProvince.getText().toString(),
                Integer.parseInt(etZip.getText().toString()));
        user.setAddress(address);
        //user.setPhotoAsString(new ImageProcessor().toUTF8(((BitmapDrawable)ivPicture.getDrawable()).getBitmap(), true));
        //user.setPhotoAsString(new ImageProcessor().toUTF8(bmp, true));
        UserData.photo = bmp;

        //Alert Dialog for Confirmation builder.
        TnCDialog tnCDialog = new TnCDialog(SignUp2.this);
        tnCDialog.show();
    }

    //FOR IMAGE PICKING, DO NOT DELETE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try{
                BufferedInputStream bufferedInputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                bmp = BitmapFactory.decodeStream(bufferedInputStream);
                if (!new ImageProcessor().checkFileSize(bmp, true)) {
                    Toast.makeText(getApplicationContext(), "Please select a 1x1 picture less than 1MB.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ivPicture.setImageBitmap(bmp);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to choose file", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void uploadData(User user, String password, byte [] photo){
        /* Pass data and password to next intent that will perform
        *  essential processes and then upload data to cloud
        **/
        Intent intent = new Intent(SignUp2.this, ProcessSignUp.class);
        intent.putExtra("DATA", user);
        intent.putExtra("PASSWORD", password);
        startActivity(intent);
        finish();
    }

    private BroadcastReceiver mPositive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            uploadData(user, password, photoAsBytes);
        }
    };

    private BroadcastReceiver mZip = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                etZip.setText(intent.getStringExtra("zip"));
            }
            catch (Exception e){
                Log.d("SignUp2", e.getMessage());
            }
        }
    };

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp2.this, SignUp.class);
        intent.putExtra("SIGNUPDATA", user);
        intent.putExtra("PASSWORD", password);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPositive);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mZip);
    }
}