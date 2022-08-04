package com.silong.dev;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.Object.Address;
import com.silong.Object.User;
import com.silong.Operation.ImagePicker;
import com.silong.Operation.ImageProcessor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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

        btnCreate = (Button) findViewById(R.id.btnsignupCreate);
        ivPicture = (ImageView) findViewById(R.id.ivsignupPicture);
        etAddress = (EditText) findViewById(R.id.tfsignupAddress);
        spinBarangay = (Spinner) findViewById(R.id.spsignupBarangay);
        spinMunicipality = (EditText) findViewById(R.id.spsignupMunicipality);
        spinProvince = (EditText) findViewById(R.id.spsignupProvince);
        etZip = (EditText) findViewById(R.id.tfsignupZip);

        spinMunicipality.setText("City of San Jose del Monte");
        spinProvince.setText("Bulacan");
        etZip.setText("3023");

        //Concatenate all the barangays
        String[] first = getResources().getStringArray(R.array.barangay_3023);
        String[] second = getResources().getStringArray(R.array.barangay_3024);

        String[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);

        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<String>(this, R.layout.drop_down_items, both) {
            @Override
            public int getCount() {
                return both.length-1;
            }
        };
        spinBarangay.setAdapter(barangayAdapter);
        spinBarangay.setSelection(both.length-1);

        spinBarangay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedBarangay = spinBarangay.getSelectedItem().toString();
                for (String s : first){
                    if (s.equals(selectedBarangay))
                        etZip.setText("3023");
                }
                for (String s : second){
                    if (s.equals(selectedBarangay))
                        etZip.setText("3024");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Image Picker
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImagePicker (SignUp2.this, PICK_IMAGE);
            }
        });

        //Toast to Remind Silong is for SJDM Only
        spinMunicipality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Silong is exclusive to San Jose del Monte City.", Toast.LENGTH_SHORT).show();
            }
        });

        //Toast to Remind Silong is for Bulacan Only
        spinProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Silong is exclusive to San Jose del Monte City.", Toast.LENGTH_SHORT).show();
            }
        });

        //Toast to Remind zip is not editable.
        etZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Zipcode is not editable.", Toast.LENGTH_SHORT).show();
            }
        });

        //Create Button, opens Dialog and TNC
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SignUp2.this);
                builder.setTitle(Html.fromHtml("<b>"+"Terms and Conditions"+"</b>"));
                builder.setIcon(getDrawable(R.drawable.circlelogo_gradient));
                builder.setBackground(getDrawable(R.drawable.dialog_bg));
                builder.setMessage(getResources().getString(R.string.msg));

                LinearLayout tnc_layout = new LinearLayout(SignUp2.this);
                tnc_layout.setOrientation(LinearLayout.VERTICAL);
                tnc_layout.setVerticalGravity(10);
                TextView tnc_tv = new TextView(SignUp2.this);
                tnc_tv.setText("Terms and Conditions");
                tnc_tv.setTextColor(getColor(R.color.purple_700));
                tnc_tv.setPadding(60,0,0,0);
                tnc_layout.addView(tnc_tv);
                builder.setView(tnc_layout);

                builder.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadData(user, password, photoAsBytes);
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //codes here
                    }
                });
                builder.show();

                //Shows TNC Screen
                tnc_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTnc();
                    }
                });
            }

        });
    }

    //FOR IMAGE PICKING, DO NOT DELETE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try{
                BufferedInputStream bufferedInputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                bmp = BitmapFactory.decodeStream(bufferedInputStream);
                ivPicture.setImageBitmap(bmp);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to choose file", Toast.LENGTH_SHORT).show();
            }

        }
    }

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

    public void showTnc(){
        Intent i = new Intent(SignUp2.this, TermsConditions.class);
        startActivity(i);
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
}