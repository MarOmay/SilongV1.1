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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.chip.Chip;
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
import com.silong.CustomView.HomepageExitDialog;
import com.silong.CustomView.LoadingDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetStatus;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Object.User;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.Utility;
import com.silong.Task.AccountStatusChecker;
import com.silong.Task.PetStatusUpdater;
import com.silong.Task.SyncPetRecord;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Homepage extends AppCompatActivity {

    public static boolean DOG = true;
    public static boolean CAT = true;
    public static boolean PUPPY = true;
    public static boolean YOUNG = true;
    public static boolean OLD = true;
    public static boolean MALE = true;
    public static boolean FEMALE = true;
    public static boolean LIKED = false;

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

    private ArrayList<Pet> tempPetList = new ArrayList<>();
    private Pet CURRENT_PET = new Pet();

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

        //Receive trigger from ASC task initiated by Apply button
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBeginApplication, new IntentFilter("account-status-active"));

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
        Intent i = new Intent(Homepage.this, Filter.class);
        startActivity(i);
        finish();
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
            //making a copy of the filtered list as koloda listener reference
            tempPetList = filterList();

            if (tempPetList.size() < 3){
                for (Pet pet : tempPetList)
                    tempPetList.add(pet);
            }

            ArrayList<Pet> copyOfTempPetList = (ArrayList<Pet>) tempPetList.clone();

            SwipeAdapter adapter = new SwipeAdapter(this, copyOfTempPetList);
            koloda.setAdapter(adapter);
        }
        catch (Exception e){
            Log.d("Homepage-lK", e.getMessage() != null ? e.getMessage() : "Error");
        }
    }

    private ArrayList<Pet> filterList(){
        ArrayList<Pet> filteredList = new ArrayList<>();

        for (Pet pet : UserData.pets){

            if (pet.getType() == PetType.DOG && !Homepage.DOG)
                continue;
            else if (pet.getType() == PetType.CAT && !Homepage.CAT)
                continue;

            if (pet.getGender() == Gender.MALE && !Homepage.MALE)
                continue;
            else if (pet.getGender() == Gender.FEMALE && !Homepage.FEMALE)
                continue;

            if (!Homepage.PUPPY && pet.getAge() == PetAge.PUPPY)
                continue;
            if (!Homepage.YOUNG && pet.getAge() == PetAge.YOUNG)
                continue;
            if (!Homepage.OLD && pet.getAge() == PetAge.OLD)
                continue;
            
            filteredList.add(pet);
            Log.d("DEBUGGER>>>", "Added");
        }

        Log.d("DEBUGGER>>>", "Size: " + filteredList.size());

        return filteredList;
    }

    private void setKolodaListener(){
        CURRENT_PET = tempPetList.get(0);
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
                i++;
                while (i >= tempPetList.size())
                    i -= tempPetList.size();
                Log.d("DEBUGGER>>>", "value of i: " + i);
                SwipeAdapter swipeAdapter = (SwipeAdapter) koloda.getAdapter();
                //swipeAdapter.insert(UserData.pets.get(i+1));
                swipeAdapter.insert(tempPetList.get(i));

                if (tempPetList.size() == i+1){
                    CURRENT_PET = tempPetList.get(0);
                }
                else {
                    CURRENT_PET = tempPetList.get(i+1);
                }
            }

            @Override
            public void onCardSwipedRight(int i) {

                //insert the removed record to the end of arraylist
                i++;
                while (i >= tempPetList.size())
                    i -= tempPetList.size();
                Log.d("DEBUGGER>>>", "value of i: " + i);
                SwipeAdapter swipeAdapter = (SwipeAdapter) koloda.getAdapter();
                swipeAdapter.insert(tempPetList.get(i));

                if (tempPetList.size() == i+1){
                    CURRENT_PET = tempPetList.get(0);
                }
                else {
                    CURRENT_PET = tempPetList.get(i+1);
                }
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

    public void onPressedApply(View view){
        if (Utility.internetConnection(getApplicationContext())){
            AccountStatusChecker accountStatusChecker = new AccountStatusChecker(Homepage.this);
            accountStatusChecker.execute();
        }
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

    private BroadcastReceiver mBeginApplication = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                Log.d("DEBUGGER>>>", "P: " + CURRENT_PET.getPetID());
                Log.d("DEBUGGER>>>", "C: " + CURRENT_PET.getColor());

                //launch timeline
                //check pet status
                //if petStatus != active, goto Homepage
                //inform users
                //else, PetStatusUpdater petStatusUpdater = new PetStatusUpdater(Homepage.this, CURRENT_PET.getPetID(), false);
                //write file indicator
                //write all database updates

            }
            catch (Exception e){
                Log.d("Homepage-mBA", "Invalid trigger");
            }

        }
    };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else {
            HomepageExitDialog homepageExitDialog = new HomepageExitDialog(Homepage.this);
            homepageExitDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBeginApplication);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mAvatarReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNameReceiver);
        super.onDestroy();
    }
}