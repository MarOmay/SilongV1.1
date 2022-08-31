package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyachi.stepview.VerticalStepView;

import java.util.Arrays;
import java.util.List;

public class Timeline extends AppCompatActivity {

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

        timelineDrawer = (DrawerLayout) findViewById(R.id.timelineDrawer);
        View view = findViewById(R.id.headerLayout);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        menuImgview = (ImageView) findViewById(R.id.menuImgview);
        closeDrawerBtn = (ImageView) findViewById(R.id.closeDrawerBtn);
        timelineHeader = (TextView) findViewById(R.id.timelineHeader);
        timelineBody = (TextView) findViewById(R.id.timelineBody);
        timelineStepView = (VerticalStepView) findViewById(R.id.timelineStepview);
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

        //StepView
        List<String> sources = Arrays.asList(getResources().getStringArray(R.array.steps));

        timelineStepView.reverseDraw(false)
                .setStepViewTexts(sources)
                .setLinePaddingProportion(0.65f)
                //complete
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, R.color.pink))
                .setStepViewComplectedTextColor(ContextCompat.getColor(this, R.color.black))
                .setStepsViewIndicatorCompleteIcon(getDrawable(R.drawable.task_complete))
                //uncompleted
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, R.color.gray))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this,R.color.gray))
                .setStepsViewIndicatorAttentionIcon(getDrawable(R.drawable.task_ongoing))
                //default
                .setStepsViewIndicatorDefaultIcon(getDrawable(R.drawable.task_icon))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.gray))
                .setTextSize(13);

        timelineStepView.setStepsViewIndicatorComplectingPosition(0);

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
    }

}