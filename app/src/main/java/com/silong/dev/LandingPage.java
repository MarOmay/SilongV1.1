package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.silong.CustomView.RelaunchNotifier;
import com.silong.Operation.Utility;

public class LandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();

        DrawerLayout landingDrawer;
        TextView headerTitle;
        ImageView filterImgview, messageImgview, menuImgview, closeDrawerBtn, avatarImgview;
        Button landingTimelineBtn, landingPetGalleryBtn;
        TextView petsAvailableCount, petsProcessCount, livesSavedCount;

        landingDrawer = (DrawerLayout) findViewById(R.id.landingDrawer);
        View view = findViewById(R.id.headerLayout);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        avatarImgview = (ImageView) findViewById(R.id.avatarImgview);

        filterImgview.setVisibility(View.INVISIBLE);
        headerTitle.setVisibility(View.INVISIBLE);
    }


}