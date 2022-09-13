package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AboutTheOffice extends AppCompatActivity {

    ImageView facebookImgview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_the_office);
        getSupportActionBar().hide();

        facebookImgview = (ImageView) findViewById(R.id.facebookImgview);

    }

    public void onPressedFB(View view){
        try {
            //open fb page using fb app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/CityVetOfficeCSJDM"));
            startActivity(intent);
        }
        catch (Exception e){
            //open fb page using browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/CityVetOfficeCSJDM")));
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