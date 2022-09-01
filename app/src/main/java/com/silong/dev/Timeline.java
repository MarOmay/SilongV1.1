package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.VerticalStepView;
import com.google.firebase.auth.FirebaseAuth;
import com.silong.CustomView.CustomStepView;
import com.silong.CustomView.ExitDialog;

public class Timeline extends AppCompatActivity {

    //timeline stages
    public static final int SEND_REQUEST = 0;
    public static final int AWAITING_APPROVAL = 1;
    public static final int REQUEST_APPROVED = 2;
    public static final int SET_APPOINTMENT = 3;
    public static final int APPOINTMENT_CONFIRMED = 4;
    public static final int ADOPTION_SUCCESSFUL = 5;
    public static final int FINISHED = 6;

    DrawerLayout timelineDrawer;
    ImageView filterImgview, menuImgview, closeDrawerBtn;
    TextView headerTitle, timelineHeader, timelineBody;
    VerticalStepView timelineStepView;
    LinearLayout timelineCancelLayout, timelineSetAppLayout, timelineHomeLayout;
    Button timelineCancelBtn, timelineSetAppBtn, timelineSetAppCancelBtn, timelineHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        getSupportActionBar().hide();

        //Receive logout trigger
        LocalBroadcastManager.getInstance(this).registerReceiver(mLogoutReceiver, new IntentFilter("logout-user"));

        timelineDrawer = (DrawerLayout) findViewById(R.id.timelineDrawer);
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

        setCurrentStage(FINISHED);

        populateMenu();

    }

    public void onPressedMenu(View view){
        timelineDrawer.openDrawer(GravityCompat.END);
    }

    public void onPressedCloseDrawer(View view){
        timelineDrawer.closeDrawer(GravityCompat.END);
    }

    public void onPressedAboutOffice(View view){
        Intent i = new Intent(Timeline.this, AboutTheOffice.class);
        startActivity(i);
    }

    public void onPressedAboutUs(View view){
        Intent z = new Intent(Timeline.this, AboutUs.class);
        startActivity(z);
    }

    public void onEditProfilePressed(View view){
        Intent i = new Intent(Timeline.this, EditProfile.class);
        startActivity(i);
        EditProfile.FORBID_DEACTIVATION = true;
    }

    public void onPressedLogout(View view){
        ExitDialog exitDialog = new ExitDialog(Timeline.this);
        exitDialog.show();
    }

    private void populateMenu(){
        ImageView avatar = findViewById(R.id.avatarImgview);
        TextView name = findViewById(R.id.usernameTv);

        avatar.setImageBitmap(UserData.photo);
        name.setText(UserData.firstName + " " + UserData.lastName);
    }

    private void setCurrentStage(int stage){
        timelineStepView.setStepsViewIndicatorComplectingPosition(stage);
    }

    private BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log out user
            UserData.logout();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Timeline.this, "Logging out...", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Timeline.this, Splash.class);
            startActivity(i);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLogoutReceiver);
        super.onDestroy();
    }

}