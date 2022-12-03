package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.VerticalStepView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.CustomView.AppointmentDatePickerFragment;
import com.silong.CustomView.CustomStepView;
import com.silong.CustomView.ExitDialog;
import com.silong.CustomView.HomepageExitDialog;
import com.silong.CustomView.RelaunchNotifier;
import com.silong.EnumClass.PetStatus;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;
import com.silong.Task.CancellationCounter;
import com.silong.Task.SyncAdoptionHistory;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Timeline extends AppCompatActivity {

    //timeline stages
    public static final int CANCELLED = -1;
    public static final int SEND_REQUEST = 0;
    public static final int AWAITING_APPROVAL = 1;
    public static final int REQUEST_APPROVED = 2;
    public static final int SET_APPOINTMENT = 3;
    public static final int APPOINTMENT_CONFIRMED = 4;
    public static final int ADOPTION_SUCCESSFUL = 5;
    public static final int FINISHED = 6;
    public static final int DECLINED = 7;

    DrawerLayout timelineDrawer;
    SwipeRefreshLayout timelineRefreshLayout;
    ImageView filterImgview, menuImgview, closeDrawerBtn, avatarImgview;
    TextView headerTitle, timelineHeader, timelineBody;
    VerticalStepView timelineStepView;
    LinearLayout timelineCancelLayout, timelineSetAppLayout, timelineHomeLayout, headerPicLayout;
    Button timelineCancelBtn, timelineSetAppBtn, timelineSetAppCancelBtn, timelineHomeBtn;

    //new added buttons//
    Button homepageBtn, adoptHistoryBtn;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Pet PET;
    private Adoption ADOPTION = null;

    private int CURRENT_STAGE = 0;
    public static String CHOSEN_DATE, CHOSEN_TIME, FB_ID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getSupportActionBar().hide();

        Utility.log("Timeline: Launched");

        //Receive logout trigger
        LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter("logout-user"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mScheduleSelected, new IntentFilter("schedule-chosen"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mSAHReceiver, new IntentFilter("SAH-done"));

        //initialize Firebase objects
        mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");

        timelineDrawer = (DrawerLayout) findViewById(R.id.timelineDrawer);
        timelineRefreshLayout = findViewById(R.id.timelineRefreshLayout);
        View view = findViewById(R.id.headerLayout);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        menuImgview = (ImageView) findViewById(R.id.menuImgview);
        closeDrawerBtn = (ImageView) findViewById(R.id.closeDrawerBtn);
        timelineHeader = (TextView) findViewById(R.id.timelineHeader);
        timelineBody = (TextView) findViewById(R.id.timelineBody);
        timelineStepView = (CustomStepView) findViewById(R.id.timelineStepview);
        timelineCancelLayout = (LinearLayout) findViewById(R.id.timelineCancelLayout);
        timelineSetAppLayout = (LinearLayout) findViewById(R.id.timelineSetAppLayout);
        timelineHomeLayout = (LinearLayout) findViewById(R.id.timelineHomeLayout);

        timelineCancelBtn = (Button) findViewById(R.id.timelineCancelBtn);
        timelineSetAppBtn = (Button) findViewById(R.id.timelineSetAppBtn);
        timelineSetAppCancelBtn = (Button) findViewById(R.id.timelineSetAppCancelBtn);
        timelineHomeBtn = (Button) findViewById(R.id.timelineHomeBtn);

        headerPicLayout = (LinearLayout) findViewById(R.id.headerPicLayout);
        headerPicLayout.setVisibility(View.GONE);

        //hides filter button and change header to PROGRESS
        filterImgview.setVisibility(View.INVISIBLE);
        headerTitle.setText("Progress");

        //Timeline Layout initial displays
        timelineCancelLayout.setVisibility(View.VISIBLE);
        timelineSetAppLayout.setVisibility(View.GONE);
        timelineHomeLayout.setVisibility(View.GONE);
        timelineHeader.setText(R.string.congrats);
        timelineBody.setText(R.string.sendReqBody);

        avatarImgview = findViewById(R.id.avatarImgview);

        watchRTDBStatus();

        populateMenu();

        refreshTimeline();

        timelineRefreshLayout.setOnRefreshListener(refreshListener);

        //if petStatus != active, goto Homepage
        //inform users
        //else, PetStatusUpdater petStatusUpdater = new PetStatusUpdater(Homepage.this, CURRENT_PET.getPetID(), false);
        //write file indicator
        //write all database updates

    }

    public void onPressedMessage(View view){
        Utility.animateOnClick(Timeline.this, view);
        Utility.gotoMessenger(Timeline.this);
    }

    public void onPressedMenu(View view){
        Utility.animateOnClick(Timeline.this, view);

        if (UserData.userID == null){
            RelaunchNotifier relaunchNotifier = new RelaunchNotifier(Timeline.this);
            relaunchNotifier.show();
            return;
        }
        if (UserData.userID.equals("null")){
            RelaunchNotifier relaunchNotifier = new RelaunchNotifier(Timeline.this);
            relaunchNotifier.show();
            return;
        }

        populateMenu();
        avatarImgview.setImageBitmap(UserData.photo);
        timelineDrawer.openDrawer(GravityCompat.END);
    }

    public void onPressedCloseDrawer(View view){
        Utility.animateOnClick(Timeline.this, view);
        timelineDrawer.closeDrawer(GravityCompat.END);
    }

    public void onPressedAdoptionHistory(View view){
        Utility.animateOnClick(Timeline.this, view);
        Intent i = new Intent(Timeline.this, AdoptionHistory.class);
        startActivity(i);
    }

    public void onPressedHelp(View view){
        Utility.animateOnClick(Timeline.this, view);
        Intent i = new Intent(Timeline.this, Help.class);
        startActivity(i);
    }

    public void onPressedAboutOffice(View view){
        Utility.animateOnClick(Timeline.this, view);
        Intent i = new Intent(Timeline.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedAboutUs(View view){
        Utility.animateOnClick(Timeline.this, view);
        Intent z = new Intent(Timeline.this, AboutUs.class);
        startActivity(z);
    }

    public void onEditProfilePressed(View view){
        Utility.animateOnClick(Timeline.this, view);
        Intent i = new Intent(Timeline.this, EditProfile.class);
        startActivity(i);
        EditProfile.FORBID_DEACTIVATION = true;
        AccountSecuritySettings.FORBID_DEACTIVATION = true;
    }

    public void onPressedLogout(View view){
        Utility.animateOnClick(Timeline.this, view);
        ExitDialog exitDialog = new ExitDialog(Timeline.this);
        exitDialog.show();
    }

    public void onPressedHomepage(View view){
        Utility.animateOnClick(Timeline.this, view);
        Intent i = new Intent(Timeline.this, LandingPage.class);
        startActivity(i);
    }

    public void onPressedSetAppointment(View view){
        Utility.animateOnClick(Timeline.this, view);
        //check internet connection
        if (!Utility.internetConnection(Timeline.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        DialogFragment newFragment = new AppointmentDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onPressedHome(View view){
        Utility.animateOnClick(Timeline.this, view);
        //check internet connection
        if (!Utility.internetConnection(Timeline.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        //prepare data
        Map<String, Object> multiNodeMap = new HashMap<>();
        multiNodeMap.put("adoptionRequest/"+UserData.userID, null);
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/dateRequested", ADOPTION.getDateRequested());
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/status", 6);

        mReference = null;
        mReference = mDatabase.getReference();
        mReference.updateChildren(multiNodeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        resyncAdoptionHistory();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Timeline.this, "Something went wrong. please try again.", Toast.LENGTH_SHORT).show();
                        Utility.log("Timeline.oPH: " + e.getMessage());
                    }
                });

        //updateLocalStatus(FINISHED);
        //exitTimeline();
    }

    private void populateMenu(){
        ImageView avatar = findViewById(R.id.avatarImgview);
        TextView name = findViewById(R.id.usernameTv);

        avatar.setImageBitmap(UserData.photo);
        name.setText(UserData.firstName + " " + UserData.lastName);
    }

    public void onPressedCancel(View view){
        Utility.animateOnClick(Timeline.this, view);
        Utility.log("Timeline.oPC: onPressedCancel triggered");

        //check internet connection
        if (!Utility.internetConnection(Timeline.this)){
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(Timeline.this);
        loadingDialog.startLoadingDialog();
        
        Map<String, Object> multiNodeMap = new HashMap<>();
        multiNodeMap.put("Pets/"+ADOPTION.getPetID()+"/status", PetStatus.ACTIVE);
        multiNodeMap.put("recordSummary/"+ADOPTION.getPetID(), PetStatus.ACTIVE);
        multiNodeMap.put("adoptionRequest/"+UserData.userID, null);
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/dateRequested", ADOPTION.getDateRequested());
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/status", Timeline.CANCELLED);

        /*
        //set pet status to active
        DatabaseReference tempRefStatus = mDatabase.getReference().child("Pets").child(ADOPTION.getPetID()).child("status");
        tempRefStatus.setValue(PetStatus.ACTIVE);

        DatabaseReference tempReference = mDatabase.getReference().child("recordSummary").child(ADOPTION.getPetID());
        tempReference.setValue(PetStatus.ACTIVE);*/

        //set request to null in RTDB
        DatabaseReference tempRef = mDatabase.getReference();//.child("adoptionRequest").child(UserData.userID);
        tempRef.updateChildren(multiNodeMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateLocalStatus(CANCELLED);
                        Utility.log("Timeline.oPC: oPC multiNodeMap uploaded");
                        //resyncAdoptionHistory();

                        /*
                        Log.d("DEBUGGER>>>", "Cancellation ADOPTION.getPetID(): " + ADOPTION.getPetID());

                        //archive to user's RTDB
                        DatabaseReference tempRef2 = mDatabase.getReference().child("Users").child(UserData.userID).child("adoptionHistory").child(ADOPTION.getPetID());
                        Map<String, Object> map = new HashMap<>();
                        map.put("dateRequested", ADOPTION.getDateRequested());
                        map.put("status", Timeline.CANCELLED);
                        tempRef2.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                //increment rtdb cancellation value
                                CancellationCounter cc = new CancellationCounter(UserData.userID);
                                cc.execute();

                                //notify user
                                EmailNotif emailNotif = new EmailNotif(UserData.email, Timeline.CANCELLED, ADOPTION);
                                emailNotif.sendNotif();

                                //delete local copy of current adoption
                                UserData.deleteAdoptionByID(Timeline.this, ADOPTION.getPetID());

                                //return to Splash
                                loadingDialog.dismissLoadingDialog();
                                Intent intent = new Intent(Timeline.this, Splash.class);
                                startActivity(intent);
                                Timeline.this.finish();

                            }
                        });*/

                        //increment rtdb cancellation value
                        CancellationCounter cc = new CancellationCounter(UserData.userID);
                        cc.execute();

                        //notify user
                        EmailNotif emailNotif = new EmailNotif(UserData.email, Timeline.CANCELLED, ADOPTION);
                        emailNotif.sendNotif();

                        //delete local copy of current adoption
                        UserData.deleteAdoptionByID(Timeline.this, ADOPTION.getPetID());

                        //return to Splash
                        loadingDialog.dismissLoadingDialog();
                        Intent intent = new Intent(Timeline.this, Splash.class);
                        startActivity(intent);
                        Timeline.this.finish();

                    }
                });

    }

    private void refreshTimeline(){
        UserData.populateAdoptions(Timeline.this);
        for (Adoption adoption : UserData.adoptionHistory){
            Utility.log("Timeline.rT: Adoption on device: " + adoption.getDateRequested() + " petID: " + adoption.getPetID() + " status: " + adoption.getStatus());
            switch (adoption.getStatus()){
                case SEND_REQUEST:
                case AWAITING_APPROVAL:
                case REQUEST_APPROVED:
                case SET_APPOINTMENT:
                case APPOINTMENT_CONFIRMED:
                case ADOPTION_SUCCESSFUL:   ADOPTION = adoption; break;
            }
        }

        if (ADOPTION == null){
            //exitTimeline();
            resyncAdoptionHistory();
            return;
        }

        Utility.log("Timeline.rT: Final Adoption on device: " + ADOPTION.getDateRequested() + " petID: " + ADOPTION.getPetID());

        PET = UserData.getPet(ADOPTION.getPetID());

        CURRENT_STAGE = ADOPTION.getStatus();

        timelineStepView.setVisibility(View.INVISIBLE);
        timelineStepView.setStepsViewIndicatorComplectingPosition(CURRENT_STAGE +1);
        timelineStepView.setVisibility(View.VISIBLE);
        Utility.log("Timeline.rT: Setting timeline to " + CURRENT_STAGE);

        switch (CURRENT_STAGE){
            case SEND_REQUEST:
                timelineCancelLayout.setVisibility(View.VISIBLE);
                timelineSetAppLayout.setVisibility(View.GONE);
                timelineHomeLayout.setVisibility(View.GONE);
                timelineHeader.setText(R.string.sendReqBody);
                timelineBody.setText(R.string.sendReqBody);
                break;

            case AWAITING_APPROVAL:
                timelineCancelLayout.setVisibility(View.VISIBLE);
                timelineSetAppLayout.setVisibility(View.GONE);
                timelineHomeLayout.setVisibility(View.GONE);
                timelineHeader.setText(R.string.congrats);
                timelineBody.setText(R.string.awaitingBody);
                break;

            case REQUEST_APPROVED:
                timelineCancelLayout.setVisibility(View.GONE);
                timelineSetAppLayout.setVisibility(View.VISIBLE);
                timelineHomeLayout.setVisibility(View.GONE);
                timelineHeader.setText(R.string.reqApprovedHeader);
                timelineBody.setText(R.string.reqApprovedBody);
                break;

            case SET_APPOINTMENT:

                try {
                    ADOPTION.getAppointmentDate().replace("*",":");
                }
                catch (Exception e){
                    Utility.log("Timeline: " + e.getMessage());

                    //delete surplus data
                    for (File file : getFilesDir().listFiles()){
                        if (file.getAbsolutePath().contains("adoption-")){
                            file.delete();
                        }
                    }

                    Intent intent = new Intent(Timeline.this, HorizontalProgressBar.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                    timelineRefreshLayout.setRefreshing(false);
                    finish();
                }

                timelineCancelLayout.setVisibility(View.INVISIBLE);
                timelineSetAppLayout.setVisibility(View.GONE);
                timelineHomeLayout.setVisibility(View.GONE);
                timelineHeader.setText(R.string.setAppointmentHeader);
                timelineBody.setText("on " + ADOPTION.getAppointmentDate().replace("*",":") + "\n\nNote:\nThe office reserves the right to reschedule the appointment without prior notice.");
                break;

            case APPOINTMENT_CONFIRMED:
                timelineCancelLayout.setVisibility(View.INVISIBLE);
                timelineSetAppLayout.setVisibility(View.GONE);
                timelineHomeLayout.setVisibility(View.GONE);
                timelineHeader.setText(R.string.appointmentConfirmedHeader);
                String body1 = getResources().getString(R.string.appointmentConfirmedBody1);
                String body2 = getResources().getString(R.string.appointmentConfirmedBody2);
                timelineBody.setText(body1 + " " + ADOPTION.getAppointmentDate().replace("*",":") + body2);
                break;

            case ADOPTION_SUCCESSFUL:
                timelineCancelLayout.setVisibility(View.GONE);
                timelineSetAppLayout.setVisibility(View.GONE);
                timelineHomeLayout.setVisibility(View.VISIBLE);
                timelineHeader.setText(R.string.congrats);
                timelineBody.setText(R.string.successBody);
                break;

            case FINISHED:
                mReference = null;
                mReference = mDatabase.getReference().child("adoptionRequest").child(UserData.userID);
                mReference.setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //go back to HorizontalProgressBar
                                //exitTimeline();
                                resyncAdoptionHistory();
                            }
                        });
                break;

            case DECLINED:
                Toast.makeText(this, "Request declined", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void restartTimeline(){
        mReference = null;
        Log.d("DEBUGGER>>>", "restarting");
        Intent gotoTimeline = new Intent(Timeline.this, Timeline.class);
        startActivity(gotoTimeline);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        finish();
    }

    private void updatePetStatus(String petID){
        if (petID == null){
            return;
        }

        Utility.log("Timeline.uPS: TRACK petID: (uPS) " + petID);
        Utility.log("Timeline.uPS: TRACK getPetID: (uPS) " + ADOPTION.getPetID());

        String date = Utility.dateToday();

        Map<String, Object> multiNodeMap = new HashMap<>();
        multiNodeMap.put("adoptionRequest/"+UserData.userID+"/status", "1");
        multiNodeMap.put("adoptionRequest/"+UserData.userID+"/dateRequested", date);
        multiNodeMap.put("adoptionRequest/"+UserData.userID+"/petID", ADOPTION.getPetID());
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/status", AWAITING_APPROVAL);
        multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/dateRequested", date);

        DatabaseReference tempRef = mDatabase.getReference();
        tempRef.updateChildren(multiNodeMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        resyncAdoptionHistory();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Timeline.this, "Operation failed", Toast.LENGTH_SHORT).show();
                        Utility.log("Timeline.uPS: " + e.getMessage());
                    }
                });

        /*
        //write to RTDB
        DatabaseReference tempRef = mDatabase.getReference().child("adoptionRequest").child(UserData.userID);
        Map<String, Object> map = new HashMap<>();
        map.put("status", "1");
        map.put("dateRequested", Utility.dateToday());
        map.put("petID", ADOPTION.getPetID());

        tempRef.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateLocalStatus(AWAITING_APPROVAL);
                        updateRemoteStatus(AWAITING_APPROVAL);
                        //restartTimeline();
                        refreshTimeline();
                    }
                });
        */

    }

    private void watchRTDBStatus(){

        Utility.log("Timeline.wRS: watchRTDBStatus: started");
        Utility.log("Timeline.wRS: id : " + UserData.userID);

        if (UserData.userID == null){
            return;
        }

        DatabaseReference mref = mDatabase.getReference().child("adoptionRequest").child(UserData.userID);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("status").getValue() != null){

                    if (snapshot.child("status").getValue().toString().equals(String.valueOf(ADOPTION.getStatus()))){

                        return;
                    }

                    int status = Integer.valueOf(snapshot.child("status").getValue().toString());

                    Utility.log("Timeline.wRS: statusFromCLoud: " + status + "  curStatus: " + ADOPTION.getStatus());

                    updateLocalStatus(status);

                    /*
                    try {
                        String date = snapshot.child("appointmentDate").getValue().toString();
                        String time = snapshot.child("appointmentTime").getValue().toString();
                        updateLocalStatus(status, date, time);

                        String dateTime = date + " " + time;
                        if (!dateTime.equals(ADOPTION.getAppointmentDate())){
                            restartTimeline();
                            //refreshTimeline();
                        }
                    }
                    catch (Exception e){
                        updateLocalStatus(status);
                        Utility.log("Timeline.wRS.oDC: " + e.getMessage());
                    }*/

                    //updateRemoteStatus(status);

                    Utility.log("Timeline.wRS: Status - " + status);

                    if (status == REQUEST_APPROVED){
                        timelineSetAppCancelBtn.setEnabled(true);
                        timelineSetAppCancelBtn.setClickable(true);
                        timelineSetAppBtn.setEnabled(true);
                        timelineSetAppBtn.setClickable(true);
                    }

                    if (status == DECLINED){
                        resyncAdoptionHistory();
                        return;
                    }
                    else if (ADOPTION.getStatus() != status ){
                        //restartTimeline();
                        //mReference = null;
                        //refreshTimeline();
                        SyncAdoptionHistory syncAdoptionHistory = new SyncAdoptionHistory(Timeline.this, UserData.userID, false);
                        syncAdoptionHistory.execute();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.log("Timeline.wRS.Oc: " + error.getMessage());
            }
        });
    }

    private void archiveAdoption(){
        DatabaseReference tempRef = mDatabase.getReference().child("Users").child(UserData.userID).child("adoptionHistory").child(ADOPTION.getPetID());

        Map<String, Object> map = new HashMap<>();
        map.put("dateRequested", ADOPTION.getDateRequested());
        map.put("status", 6);

        tempRef.updateChildren(map);

        Utility.log("Timeline.aA: aA map uploaded");

    }

    private void resyncAdoptionHistory(){
        Intent intent = new Intent(Timeline.this, HorizontalProgressBar.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        finish();
    }

    private void updateLocalStatus(int status, String date, String time){
        //update adoption- file
        try (FileOutputStream fileOutputStream = Timeline.this.openFileOutput( "adoption-" + ADOPTION.getPetID(), Context.MODE_APPEND)) {
            String data = "status:" + status + ";\n";
            data += "appointmentDate:" + date + " " + time.replace(":","*") + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Utility.log("Timeline.uLS: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Can't update adoption-.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocalStatus(int status){
        //update adoption- file
        try (FileOutputStream fileOutputStream = Timeline.this.openFileOutput( "adoption-" + ADOPTION.getPetID(), Context.MODE_APPEND)) {
            String data = "status:" + status + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Utility.log("Timeline.uLS: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Can't update adoption-.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRemoteStatus(int status){
        DatabaseReference remoteRef = mDatabase.getReference().child("Users").child(UserData.userID)
                .child("adoptionHistory").child(String.valueOf(ADOPTION.getPetID())).child("status");

        remoteRef.setValue(status);
    }

    private void resyncSystem(){
        Intent intent = new Intent(Timeline.this, HorizontalProgressBar.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
        timelineRefreshLayout.setRefreshing(false);
        finish();
    }

    private BroadcastReceiver mScheduleSelected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //check if crucial info are not missing
            if (UserData.userID == null){
                RelaunchNotifier relaunchNotifier = new RelaunchNotifier(Timeline.this);
                relaunchNotifier.show();
                return;
            }
            if (UserData.userID.equals("null")){
                RelaunchNotifier relaunchNotifier = new RelaunchNotifier(Timeline.this);
                relaunchNotifier.show();
                return;
            }

            timelineSetAppCancelBtn.setEnabled(false);
            timelineSetAppCancelBtn.setClickable(false);
            timelineSetAppBtn.setEnabled(false);
            timelineSetAppBtn.setClickable(false);

            Map<String, Object> multiNodeMap = new HashMap<>();
            multiNodeMap.put("adoptionRequest/"+UserData.userID+"/status", String.valueOf(Timeline.SET_APPOINTMENT));
            multiNodeMap.put("adoptionRequest/"+UserData.userID+"/dateRequested", ADOPTION.getDateRequested());
            multiNodeMap.put("adoptionRequest/"+UserData.userID+"/appointmentDate", CHOSEN_DATE);
            multiNodeMap.put("adoptionRequest/"+UserData.userID+"/appointmentTime", CHOSEN_TIME.replace(":","*"));
            multiNodeMap.put("adoptionRequest/"+UserData.userID+"/petID", ADOPTION.getPetID());
            multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/status", Timeline.SET_APPOINTMENT);
            multiNodeMap.put("Users/"+UserData.userID+"/adoptionHistory/"+ADOPTION.getPetID()+"/dateRequested", ADOPTION.getDateRequested());

            /*
            Map<String, Object> map = new HashMap<>();
            map.put("appointmentDate", CHOSEN_DATE);
            map.put("appointmentTime", CHOSEN_TIME.replace(":","*"));
            map.put("status", "3");
            map.put("petID", String.valueOf(ADOPTION.getPetID()));
            */

            DatabaseReference tempRefDate = mDatabase.getReference();
            tempRefDate.updateChildren(multiNodeMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Utility.log("Timeline.mSS: mSS map uploaded");
                            updateLocalStatus(Integer.parseInt(ADOPTION.getPetID()), CHOSEN_DATE, CHOSEN_TIME.replace(":","*"));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Timeline.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                            Utility.log("Timeline.mSS: " + e.getMessage());
                            timelineSetAppCancelBtn.setEnabled(true);
                            timelineSetAppCancelBtn.setClickable(true);
                            timelineSetAppBtn.setEnabled(true);
                            timelineSetAppBtn.setClickable(true);
                        }
                    });

            /*
            DatabaseReference tempRefDate = mDatabase.getReference().child("adoptionRequest").child(UserData.userID).child("appointmentDate");
            tempRefDate.setValue(CHOSEN_DATE);
            DatabaseReference tempRefTime = mDatabase.getReference().child("adoptionRequest").child(UserData.userID).child("appointmentTime");
            tempRefTime.setValue(CHOSEN_TIME.replace(":","*"));

            //write new details to file
            try (FileOutputStream fileOutputStream = Timeline.this.openFileOutput( "adoption-" + ADOPTION.getDateRequested(), Context.MODE_APPEND)) {
                String data = "appointmentDate:" + CHOSEN_DATE + " " + CHOSEN_TIME.replace(":","*") + ";\n";
                fileOutputStream.write(data.getBytes());
                fileOutputStream.flush();
            }
            catch (Exception e){
                Log.d("DEBUGGER>>>", e.getMessage());
                Toast.makeText(getApplicationContext(), "Can't update adoption-.", Toast.LENGTH_SHORT).show();
            }

            DatabaseReference tempRef = mDatabase.getReference().child("adoptionRequest").child(UserData.userID).child("status");
            tempRef.setValue("3");
            */
        }
    };

    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log out user
            UserData.logout(Timeline.this);
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Timeline.this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Timeline.this, Splash.class);
            startActivity(i);
            finish();
        }
    };

    private BroadcastReceiver mSAHReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshTimeline();
        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            resyncSystem();

        }
    };

    @Override
    public void onBackPressed() {
        if (timelineDrawer.isDrawerOpen(GravityCompat.END)){
            timelineDrawer.closeDrawer(GravityCompat.END);
        }
        else {
            Intent intent = new Intent(Timeline.this, LandingPage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mScheduleSelected);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSAHReceiver);
        super.onDestroy();
    }

}