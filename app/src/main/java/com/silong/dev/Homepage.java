package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silong.CustomDialog.ExitDialog;
import com.silong.CustomDialog.FilterDialog;
import com.silong.Operation.SyncData;
import com.yalantis.library.Koloda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    /* APP-SPECIFIC FILES */
    protected static File USERDATA;
    protected static File AVATARDATA;
    protected static File ADOPTIONDATA;
    protected static File CHATDATA;
    protected static File CHATCONFIG;
    protected static File PETDATA;
    protected static File FAVORITECONFIG;

    //Firebase objects
    private FirebaseAnalytics mAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private SwipeAdapter adapter;
    private List<Integer> list;
    Koloda koloda;
    DrawerLayout drawerLayout;

    TextView headerTitle;
    ImageView filterImgview, messageImgview, menuImgview, closeDrawerBtn;
    Button applyBtn, aboutOfficeBtn, aboutUsBtn,exitBtn;
    ImageView heartIcon;

    ImageView avatarImgview;
    TextView usernameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getSupportActionBar().hide();

        //Initialize Files
        USERDATA = new File(getFilesDir(),"user.dat");
        AVATARDATA = new File(getFilesDir(),"avatar.dat");
        ADOPTIONDATA = new File(getFilesDir(),"adoption.dat");
        CHATDATA = new File(getFilesDir(),"chat.dat");
        CHATCONFIG = new File(getFilesDir(),"chat.config");
        PETDATA = new File(getFilesDir(),"pet.dat");
        FAVORITECONFIG = new File(getFilesDir(),"favorite.config");

        //Initialize Firebase Objects
        mAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        //Receive logout trigger
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mLogoutReceiver, new IntentFilter("logout-user"));

        //Receive drawer avatar trigger
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mAvatarReceiver, new IntentFilter("update-avatar"));

        //Receive drawer name trigger
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mNameReceiver, new IntentFilter("update-name"));

        //Initialize layout views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        View view = findViewById(R.id.headerLayout);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        messageImgview = (ImageView) findViewById(R.id.messageImgview);
        menuImgview = (ImageView) findViewById(R.id.menuImgview);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        heartIcon = (ImageView) findViewById(R.id.heartIcon);
        closeDrawerBtn = (ImageView) findViewById(R.id.closeDrawerBtn);
        aboutOfficeBtn = (Button) findViewById(R.id.aboutOfficeBtn);
        aboutUsBtn = (Button) findViewById(R.id.aboutUsBtn);
        exitBtn = (Button) findViewById(R.id.exitBtn);

        avatarImgview = findViewById(R.id.avatarImgview);
        usernameTv = findViewById(R.id.usernameTv);


        /*--------------------------- MANUAL OnClickListener ---------------------------*/

        //opens filter dialog
        filterImgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog filterDialog = new FilterDialog(Homepage.this);
                filterDialog.show();
            }
        });

        //opens navigation drawer
        menuImgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        /*--------------------------- END OnClickListener ---------------------------*/


        //Koloda swipe
        koloda = findViewById(R.id.koloda);
        list = new ArrayList<>();

        adapter = new SwipeAdapter(this, list);
        koloda.setAdapter(adapter);

        UserData.populate(this);
    }

    public void onPressedAdoptionHistory(View view){

    }

    public void onPressedAboutOffice(View view){
        Intent i = new Intent(Homepage.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedAboutUs(View view){
        Intent z = new Intent(Homepage.this, AboutUs.class);
        startActivity(z);
    }

    public void onPressedCloseDrawer(View view){
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    public void onPressedLogout(View view){
        ExitDialog exitDialog = new ExitDialog(Homepage.this);
        exitDialog.show();
    }

    private BroadcastReceiver mAvatarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            avatarImgview.setImageBitmap(UserData.photo);
        }
    };

    private BroadcastReceiver mNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            usernameTv.setText(UserData.firstName + " " + UserData.lastName);
        }
    };

    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log out user
            avatarImgview.setImageResource(R.drawable.circlelogo_white);
            UserData.logout();
            mAuth.signOut();
            Toast.makeText(Homepage.this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Homepage.this, Splash.class);
            startActivity(i);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAvatarReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNameReceiver);
        super.onDestroy();
    }
}