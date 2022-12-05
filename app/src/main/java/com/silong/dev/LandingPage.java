package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.ExitDialog;
import com.silong.CustomView.HomepageExitDialog;
import com.silong.CustomView.RelaunchNotifier;
import com.silong.Object.Adoption;
import com.silong.Operation.Utility;
import com.silong.Task.AccountStatusChecker;

import java.io.File;

public class LandingPage extends AppCompatActivity {

    private DrawerLayout landingDrawer;
    private TextView headerTitle, usernameTv;
    private ImageView filterImgview, messageImgview, menuImgview, closeDrawerBtn, avatarImgview, headerPic;
    private Button landingTimelineBtn, landingPetGalleryBtn;
    private TextView petsAvailableCount, petsProcessCount, livesSavedCount;

    //new elements
    ImageView landingProcessPic;
    TextView landingProcessStatus;
    LinearLayout landingProcessLayout; //i hide nalang tong layout pag walang current process, tas pag meron tsaka lang lalabas.

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private int petsInProcess = 0, livesSaved = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        getSupportActionBar().hide();

        //Initialize Files
        Homepage.USERDATA = new File(getFilesDir(),"user.dat");
        Homepage.AVATARDATA = new File(getFilesDir(),"avatar.dat");
        Homepage.ADOPTIONDATA = new File(getFilesDir(),"adoption.dat");
        Homepage.PETDATA = new File(getFilesDir(),"pet.dat");

        //Initialize Firebase Objects
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //Receive logout trigger
        LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter("logout-user"));
        //Loading dialog receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mLoadingReceiver, new IntentFilter("toggle-loading-dialog"));

        landingDrawer = (DrawerLayout) findViewById(R.id.landingDrawer);
        View view = findViewById(R.id.headerLayout);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        avatarImgview = (ImageView) findViewById(R.id.avatarImgview);
        menuImgview = (ImageView) findViewById(R.id.menuImgview);
        closeDrawerBtn = (ImageView) findViewById(R.id.closeDrawerBtn);
        messageImgview = (ImageView) findViewById(R.id.messageImgview);

        petsAvailableCount = (TextView) findViewById(R.id.petsAvailableCount);
        petsProcessCount = (TextView) findViewById(R.id.petsProcessCount);
        livesSavedCount = (TextView) findViewById(R.id.livesSavedCount);

        landingTimelineBtn = (Button) findViewById(R.id.landingTimelineBtn);
        landingPetGalleryBtn = (Button) findViewById(R.id.landingPetGalleryBtn);
        headerPic = (ImageView) findViewById(R.id.headerPic);

        //hide timeline button
        landingTimelineBtn.setVisibility(View.INVISIBLE);

        avatarImgview = findViewById(R.id.avatarImgview);
        usernameTv = findViewById(R.id.usernameTv);

        UserData.populate(this);

        avatarImgview.setImageBitmap(UserData.photo);
        usernameTv.setText(UserData.firstName + " " + UserData.lastName);

        //header customizations
        filterImgview.setVisibility(View.INVISIBLE);
        headerTitle.setTextSize(23);
        headerTitle.setGravity(Gravity.CENTER_VERTICAL);
        headerTitle.setText(UserData.firstName + " " + UserData.lastName);
        headerPic.setImageBitmap(UserData.photo);

        //reset restriction
        AccountSecuritySettings.FORBID_DEACTIVATION = false;

        //check pending adoption request
        UserData.populateAdoptions(LandingPage.this);
        if (UserData.adoptionHistory.size() > 0){
            for (Adoption adoption : UserData.adoptionHistory){
                Utility.log("Homepage: Adoption on Device - PetDI:" + adoption.getPetID() + " Status: " + adoption.getStatus());
                if (adoption.getStatus() == Timeline.DECLINED ||
                        adoption.getStatus() == Timeline.CANCELLED ||
                        adoption.getStatus() == Timeline.FINISHED)
                    continue;
                else {
                    landingTimelineBtn.setVisibility(View.VISIBLE);
                    AccountSecuritySettings.FORBID_DEACTIVATION = true;
                }
            }
        }

        //update numbers
        petsAvailableCount.setText(String.valueOf(UserData.pets.size()));
        livesSavedCount.setText(String.valueOf(UserData.adoptionHistory.size()));
        tallyPets();

        checkAccountStatus();
        detectSystemMaintenanceState();
    }

    public void onPressedTimeline(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent intent = new Intent(LandingPage.this, Timeline.class);
        startActivity(intent);
        finish();
    }

    public void onPressedGallery(View view){
        //goto Homepage
        Utility.animateOnClick(LandingPage.this, view);
        Intent i = new Intent(LandingPage.this, Homepage.class);
        startActivity(i);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        finish();
    }

    public void onPressedMessage(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Utility.gotoMessenger(LandingPage.this);
    }

    public void onPressedMenu(View view){
        Utility.animateOnClick(LandingPage.this, view);

        if (UserData.userID == null){
            RelaunchNotifier relaunchNotifier = new RelaunchNotifier(LandingPage.this);
            relaunchNotifier.show();
            return;
        }
        if (UserData.userID.equals("null")){
            RelaunchNotifier relaunchNotifier = new RelaunchNotifier(LandingPage.this);
            relaunchNotifier.show();
            return;
        }

        populateMenu();
        avatarImgview.setImageBitmap(UserData.photo);
        landingDrawer.openDrawer(GravityCompat.END);
    }

    public void onPressedCloseDrawer(View view){
        Utility.animateOnClick(LandingPage.this, view);
        landingDrawer.closeDrawer(GravityCompat.END);
    }

    public void onPressedAdoptionHistory(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent i = new Intent(LandingPage.this, AdoptionHistory.class);
        startActivity(i);
    }

    public void onPressedHelp(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent i = new Intent(LandingPage.this, Help.class);
        startActivity(i);
    }

    public void onPressedAboutOffice(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent i = new Intent(LandingPage.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedAboutUs(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent z = new Intent(LandingPage.this, AboutUs.class);
        startActivity(z);
    }

    public void onEditProfilePressed(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent i = new Intent(LandingPage.this, EditProfile.class);
        startActivity(i);
        EditProfile.FORBID_DEACTIVATION = true;
        AccountSecuritySettings.FORBID_DEACTIVATION = true;
    }

    public void onPressedLogout(View view){
        Utility.animateOnClick(LandingPage.this, view);
        ExitDialog exitDialog = new ExitDialog(LandingPage.this);
        exitDialog.show();
    }

    public void onPressedProcess(View view){
        Utility.animateOnClick(LandingPage.this, view);
        Intent i = new Intent(LandingPage.this, PetsProcess.class);
        startActivity(i);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        finish();
    }

    private void populateMenu(){
        ImageView avatar = findViewById(R.id.avatarImgview);
        TextView name = findViewById(R.id.usernameTv);

        avatar.setImageBitmap(UserData.photo);
        name.setText(UserData.firstName + " " + UserData.lastName);
    }

    private void checkAccountStatus(){
        AccountStatusChecker accountStatusChecker = new AccountStatusChecker(LandingPage.this);
        accountStatusChecker.execute();
    }

    private void tallyPets(){
        mDatabase.getReference("adoptionRequest")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            for (DataSnapshot snap : snapshot.getChildren()){
                                if (snap.getKey().equals("null")){
                                    continue;
                                }
                                else {
                                    int status = Integer.parseInt(snap.child("status").getValue().toString());
                                    if (status >= Timeline.AWAITING_APPROVAL && status <= Timeline.APPOINTMENT_CONFIRMED){
                                        petsInProcess++;
                                        petsProcessCount.setText(String.valueOf(petsInProcess));
                                    }
                                }
                            }
                        }
                        catch (Exception e){
                            Utility.log("LandingPage.tallyPets: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Utility.log("LandingPage.tallyPets: " + error.getMessage());
                    }
                });
    }

    private void detectSystemMaintenanceState(){
        mDatabase.getReference("publicInformation/isSystemMaintenance")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Object isSystemMaintenance = snapshot.getValue();

                        if (isSystemMaintenance != null){
                            if ((boolean) isSystemMaintenance){
                                Intent intent = new Intent(LandingPage.this, UnderMaintenanceScreen.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Utility.log("LandingPage.dSM.oC: " + error.getMessage());
                    }
                });
    }


    // BROADCAST RECEIVERS

    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log out user
            avatarImgview.setImageResource(R.drawable.circlelogo_white);
            UserData.logout(LandingPage.this);
            mAuth.signOut();
            Toast.makeText(LandingPage.this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LandingPage.this, Splash.class);
            startActivity(i);
            finish();
        }
    };

    private LoadingDialog loadingDialog = new LoadingDialog(LandingPage.this);
    private BroadcastReceiver mLoadingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                boolean toggle = intent.getBooleanExtra("toggle", false);
                if (toggle)
                    loadingDialog.startLoadingDialog();
                else
                    loadingDialog.dismissLoadingDialog();
            }
            catch (Exception e){
                Utility.log("Homepage.mLR: " + e.getMessage());
            }

        }
    };


    @Override
    public void onBackPressed() {
        if (landingDrawer.isDrawerOpen(GravityCompat.END)){
            landingDrawer.closeDrawer(GravityCompat.END);
        }
        else {
            HomepageExitDialog homepageExitDialog = new HomepageExitDialog(LandingPage.this);
            homepageExitDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLoadingReceiver);
        super.onDestroy();
    }

}