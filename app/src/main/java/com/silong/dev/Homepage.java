package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

        try {
            usernameTv.setText(UserData.firstName + " " + UserData.lastName);
            avatarImgview.setImageBitmap(UserData.photo);
        }
        catch (Exception e){
            Log.d("Homepage", e.getMessage());
        }

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //Update Menu Drawer details
                usernameTv.setText(UserData.firstName + " " + UserData.lastName);
                avatarImgview.setImageBitmap(UserData.photo);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //opens filter dialog
        filterImgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDia(Homepage.this);
            }
        });

        //opens navigation drawer
        menuImgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        //closes navigation drawer
        closeDrawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        });

        //takes you to About the Office screen
        aboutOfficeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Homepage.this, AboutTheOffice.class);
                startActivity(i);
            }
        });

        //takes you to About Us screen
        aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent z = new Intent(Homepage.this, AboutUs.class);
                startActivity(z);
            }
        });

        //triggers exit dialog
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog(Homepage.this);
            }
        });

        //Koloda swipe
        koloda = findViewById(R.id.koloda);
        list = new ArrayList<>();

        adapter = new SwipeAdapter(this, list);
        koloda.setAdapter(adapter);

        UserData.populate();
    }

    //Method for executing Filter Dialog
    public void filterDia(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setBackground(getDrawable(R.drawable.dialog_bg));
        builder.setView(R.layout.filter_layout);
        builder.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Codes here
            }
        });
        builder.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
        builder.show();
    }

    private void exitDialog(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(Html.fromHtml("<b>"+"Exit"+"</b>"));
        builder.setIcon(getDrawable(R.drawable.circlelogo_gradient));
        builder.setBackground(getDrawable(R.drawable.dialog_bg));
        builder.setMessage("Are you sure you want to exit the app?\nThis will log you out of your account.\n");
        builder.setPositiveButton(Html.fromHtml("<b>"+"LOGOUT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log out user
                avatarImgview.setImageResource(R.drawable.circlelogo_white);
                UserData.logout();
                mAuth.signOut();
                Toast.makeText(Homepage.this, "Logging out...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Homepage.this, Splash.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(Html.fromHtml("<b>"+"NO"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
        builder.show();
    }
}