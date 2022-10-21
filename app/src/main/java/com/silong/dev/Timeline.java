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
import com.silong.EnumClass.PetStatus;
import com.silong.Object.Adoption;
import com.silong.Object.Pet;
import com.silong.Operation.EmailNotif;
import com.silong.Operation.Utility;
import com.silong.Task.CancellationCounter;


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
    LinearLayout timelineCancelLayout, timelineSetAppLayout, timelineHomeLayout;
    Button timelineCancelBtn, timelineSetAppBtn, timelineSetAppCancelBtn, timelineHomeBtn;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private Pet PET;
    private Adoption ADOPTION = new Adoption();

    private int CURRENT_STAGE = 0;
    public static String CHOSEN_DATE, CHOSEN_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getSupportActionBar().hide();

        Log.d("DEBUGGER>>>", "Timeline launched");

        //Receive logout trigger
        LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter("logout-user"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mScheduleSelected, new IntentFilter("schedule-chosen"));

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

        mReference = null;
        mReference = mDatabase.getReference().child("adoptionRequest").child(UserData.userID);
        mReference.setValue(null);
        updateLocalStatus(FINISHED);
        exitTimeline();
    }

    private void populateMenu(){
        ImageView avatar = findViewById(R.id.avatarImgview);
        TextView name = findViewById(R.id.usernameTv);

        avatar.setImageBitmap(UserData.photo);
        name.setText(UserData.firstName + " " + UserData.lastName);
    }

    public void onPressedCancel(View view){
        Utility.animateOnClick(Timeline.this, view);
        Log.d("DEBUGGER>>>", "Cancellation triggered oPC");

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
            Log.d("DEBUGGER>>>", "Adoption on device: " + adoption.getDateRequested() + " petID: " + adoption.getPetID() + " status: " + adoption.getStatus());
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
            exitTimeline();
            return;
        }

        Log.d("DEBUGGER>>>", "Final Adoption on device: " + ADOPTION.getDateRequested() + " petID: " + ADOPTION.getPetID());

        PET = UserData.getPet(ADOPTION.getPetID());

        CURRENT_STAGE = ADOPTION.getStatus();

        timelineStepView.setVisibility(View.INVISIBLE);
        timelineStepView.setStepsViewIndicatorComplectingPosition(CURRENT_STAGE +1);
        timelineStepView.setVisibility(View.VISIBLE);
        Log.d("DEBUGGER>>>", "Setting timeline to " + CURRENT_STAGE);

        switch (CURRENT_STAGE){
            case SEND_REQUEST:
                Log.d("DEBUGGER>>>", "Updating status of " + ADOPTION.getPetID());
                updatePetStatus(ADOPTION.getPetID());
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
                timelineCancelLayout.setVisibility(View.GONE);
                timelineSetAppLayout.setVisibility(View.GONE);
                timelineHomeLayout.setVisibility(View.GONE);
                timelineHeader.setText(R.string.setAppointmentHeader);
                timelineBody.setText("on " + ADOPTION.getAppointmentDate().replace("*",":") + "\n\nNote:\nThe office reserves the right to reschedule the appointment without prior notice.");
                break;

            case APPOINTMENT_CONFIRMED:
                timelineCancelLayout.setVisibility(View.GONE);
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
                archiveAdoption();
                break;

            case FINISHED:
                mReference = null;
                mReference = mDatabase.getReference().child("adoptionRequest").child(UserData.userID);
                mReference.setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //go back to HorizontalProgressBar
                                exitTimeline();
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

        Log.d("DEBUGGER>>>", "TRACK petID: (uPS) " + petID);
        Log.d("DEBUGGER>>>", "TRACK getPetID: (uPS) " + ADOPTION.getPetID());
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

    }

    private void watchRTDBStatus(){
        Log.d("DEBUGGER>>>", "watchRTDBStatus: started");
        Log.d("DEBUGGER>>>", "id : " + UserData.userID);

        if (UserData.userID == null){
            return;
        }

        mReference = mDatabase.getReference().child("adoptionRequest").child(UserData.userID);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){

                    int status = Integer.valueOf(snapshot.child("status").getValue().toString());


                    try {
                        String date = snapshot.child("appointmentDate").getValue().toString();
                        String time = snapshot.child("appointmentTime").getValue().toString();
                        updateLocalStatus(status, date, time);

                        String dateTime = date + " " + time;
                        if (!dateTime.equals(ADOPTION.getAppointmentDate())){
                            //restartTimeline();
                            mReference = null;
                            refreshTimeline();
                        }
                    }
                    catch (Exception e){
                        updateLocalStatus(status);
                        Utility.log("Timeline.wRS.oDC: " + e.getMessage());
                    }

                    updateRemoteStatus(status);

                    Log.d("DEBUGGER>>>", "Status: " + status);



                    if (status == DECLINED){
                        Log.d("DEBUGGER>>>", "Exiting timeline");
                        exitTimeline();
                        return;
                    }
                    else if (ADOPTION.getStatus() != status ){
                        //restartTimeline();
                        mReference = null;
                        refreshTimeline();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void archiveAdoption(){
        DatabaseReference tempRef = mDatabase.getReference().child("Users").child(UserData.userID).child("adoptionHistory").child(ADOPTION.getPetID());

        Map<String, Object> map = new HashMap<>();
        map.put("dateRequested", ADOPTION.getDateRequested());
        map.put("status", 6);

        tempRef.updateChildren(map);

    }

    private void exitTimeline(){
        //return to HorizontalProgressBar
        Intent intent = new Intent(Timeline.this, HorizontalProgressBar.class);
        startActivity(intent);
        finish();
    }

    private void updateLocalStatus(int status, String date, String time){
        //update adoption- file
        try (FileOutputStream fileOutputStream = Timeline.this.openFileOutput( "adoption-" + ADOPTION.getDateRequested(), Context.MODE_APPEND)) {
            String data = "status:" + status + ";\n";
            data += "appointmentDate:" + date + " " + time.replace(":","*") + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("DEBUGGER>>>", e.getMessage());
            Toast.makeText(getApplicationContext(), "Can't update adoption-.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocalStatus(int status){
        //update adoption- file
        try (FileOutputStream fileOutputStream = Timeline.this.openFileOutput( "adoption-" + ADOPTION.getDateRequested(), Context.MODE_APPEND)) {
            String data = "status:" + status + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("DEBUGGER>>>", e.getMessage());
            Toast.makeText(getApplicationContext(), "Can't update adoption-.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRemoteStatus(int status){
        DatabaseReference remoteRef = mDatabase.getReference().child("Users").child(UserData.userID)
                .child("adoptionHistory").child(String.valueOf(ADOPTION.getPetID())).child("status");

        remoteRef.setValue(status);
    }

    private BroadcastReceiver mScheduleSelected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            Intent intent = new Intent(Timeline.this, Timeline.class);
            startActivity(intent);
            overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            timelineRefreshLayout.setRefreshing(false);
            finish();

        }
    };

    @Override
    public void onBackPressed() {
        if (timelineDrawer.isDrawerOpen(GravityCompat.END)){
            timelineDrawer.closeDrawer(GravityCompat.END);
        }
        else {
            HomepageExitDialog homepageExitDialog = new HomepageExitDialog(Timeline.this);
            homepageExitDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mScheduleSelected);
        super.onDestroy();
    }

}