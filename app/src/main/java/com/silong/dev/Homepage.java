package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.ExitDialog;
import com.silong.CustomView.FilterDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.Task.AccountStatusChecker;
import com.silong.Task.SyncPetRecord;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.io.File;
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

    DrawerLayout drawerLayout;

    TextView headerTitle, editProfileTv;
    ImageView filterImgview, messageImgview, menuImgview, closeDrawerBtn;
    Button applyBtn, aboutOfficeBtn, aboutUsBtn,exitBtn;
    ImageView heartIcon;
    Koloda koloda;

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
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

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
        koloda = findViewById(R.id.koloda);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        heartIcon = (ImageView) findViewById(R.id.heartIcon);
        closeDrawerBtn = (ImageView) findViewById(R.id.closeDrawerBtn);
        aboutOfficeBtn = (Button) findViewById(R.id.aboutOfficeBtn);
        aboutUsBtn = (Button) findViewById(R.id.aboutUsBtn);
        exitBtn = (Button) findViewById(R.id.exitBtn);
        editProfileTv = (TextView) findViewById(R.id.editProfileTv);

        avatarImgview = findViewById(R.id.avatarImgview);
        usernameTv = findViewById(R.id.usernameTv);

        UserData.populate(this);
        checkAccountStatus();
        loadKoloda();
        setKolodaListener();
    }

    public void onPressedFilter(View view){
        FilterDialog filterDialog = new FilterDialog(Homepage.this);
        filterDialog.show();
    }

    public void onPressedMessage(View view){

    }

    public void onPressedMenu(View view){
        drawerLayout.openDrawer(GravityCompat.END);
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

    public void onEditProfilePressed(View view){
        Intent i = new Intent(Homepage.this, EditProfile.class);
        startActivity(i);
    }

    private void checkAccountStatus(){
        AccountStatusChecker accountStatusChecker = new AccountStatusChecker(Homepage.this);
        accountStatusChecker.execute();
    }

    private void loadKoloda(){
        try {
            SwipeAdapter adapter = new SwipeAdapter(this, UserData.pets);
            koloda.setAdapter(adapter);
        }
        catch (Exception e){
            Log.d("Homepage-lK", e.getMessage() != null ? e.getMessage() : "Error");
        }
    }

    private void setKolodaListener(){
        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {

            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                //insert the removed record to the end of arraylist
                SwipeAdapter swipeAdapter = (SwipeAdapter) koloda.getAdapter();
                swipeAdapter.insert(UserData.pets.get(i+1));
            }

            @Override
            public void onCardSwipedRight(int i) {
                //insert the removed record to the end of arraylist
                SwipeAdapter swipeAdapter = (SwipeAdapter) koloda.getAdapter();
                swipeAdapter.insert(UserData.pets.get(i+1));
            }

            @Override
            public void onClickRight(int i) {

            }

            @Override
            public void onClickLeft(int i) {

            }

            @Override
            public void onCardSingleTap(int i) {

            }

            @Override
            public void onCardDoubleTap(int i) {

            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {

            }
        });
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAvatarReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNameReceiver);
        super.onDestroy();
    }
}