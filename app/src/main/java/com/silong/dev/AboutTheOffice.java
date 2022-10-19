package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Operation.Utility;

public class AboutTheOffice extends AppCompatActivity {

    ImageView facebookImgview;

    private String email;
    private String fbID;
    private String phone;
    private String telephone;

    private String timeFrom;
    private String timeTo;

    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_office);
        getSupportActionBar().hide();

        facebookImgview = (ImageView) findViewById(R.id.facebookImgview);

        //fetch info
        fetchPublicInfo();

    }

    public void onPressedPhone(View view){
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Mobile", phone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Utility.log("ATO.oPP: " + ex.getMessage());
        }
    }

    public void onPressedTele(View view){
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Telephone", telephone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Utility.log("ATO.oPT: " + ex.getMessage());
        }
    }

    public void onPressedEmail(View view){
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email", email);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Utility.log("ATO.oPE: " + ex.getMessage());
        }
    }

    public void onPressedFB(View view){
        try {
            //open fb page using fb app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/" + fbID));
            startActivity(intent);
        }
        catch (Exception e){
            //open fb page using browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/" + fbID)));
            Utility.log("ATO.oPP: " + e.getMessage());
        }

    }

    private void fetchPublicInfo(){
        LoadingDialog loadingDialog = new LoadingDialog(AboutTheOffice.this);
        loadingDialog.startLoadingDialog();

        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mRef = mDatabase.getReference("publicInformation");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //get info
                    try {
                        email = snapshot.child("contactInformation").child("email").getValue().toString();
                        fbID = snapshot.child("contactInformation").child("facebookPage").getValue().toString();
                        phone = snapshot.child("contactInformation").child("phone").getValue().toString();
                        telephone = snapshot.child("contactInformation").child("telephone").getValue().toString();

                        timeFrom = snapshot.child("officeSchedule").child("timeFrom").getValue().toString();
                        timeTo = snapshot.child("officeSchedule").child("timeTo").getValue().toString();

                        monday = (boolean) snapshot.child("officeSchedule").child("monday").getValue();
                        tuesday = (boolean) snapshot.child("officeSchedule").child("monday").getValue();
                        wednesday = (boolean) snapshot.child("officeSchedule").child("wednesday").getValue();
                        thursday = (boolean) snapshot.child("officeSchedule").child("thursday").getValue();
                        friday = (boolean) snapshot.child("officeSchedule").child("friday").getValue();
                        saturday = (boolean) snapshot.child("officeSchedule").child("saturday").getValue();

                    }
                    catch (Exception e){
                        Utility.log("ATO.fPI.oDC: " + e.getMessage());
                    }

                    loadingDialog.dismissLoadingDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingDialog.dismissLoadingDialog();
                    Utility.log("ATO.fPI.oDC: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            loadingDialog.dismissLoadingDialog();
            Utility.log("ATO.fPI: " + e.getMessage());
        }
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