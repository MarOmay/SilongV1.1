package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoarding extends AppCompatActivity {

    ViewPager xslideViewPager;
    LinearLayout xDotLayout;
    Button nextBtn;
    TextView skipTv;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        getSupportActionBar().hide();

        checkFirstOpen();

        nextBtn = findViewById(R.id.onBoardNextBtn);
        skipTv = findViewById(R.id.onBoardSkipTv);

        nextBtn.setVisibility(View.INVISIBLE);

        xslideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        xDotLayout = (LinearLayout) findViewById(R.id.indicatorLayout);

        viewPagerAdapter =  new ViewPagerAdapter(this);
        xslideViewPager.setAdapter(viewPagerAdapter);
        setUpIndicator(0);
        xslideViewPager.addOnPageChangeListener(viewListener);

    }

    public void setUpIndicator(int position){
        dots = new TextView[3];
        xDotLayout.removeAllViews();

        for ( int i = 0 ; i < dots.length ; i++ ){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.lightgray));
            xDotLayout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.pink));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setUpIndicator(position);

            if ( position < 2){
                nextBtn.setVisibility(View.INVISIBLE);
            }else{
                nextBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i){
        return xslideViewPager.getCurrentItem() + 1;
    }

    public void onNextPressed(View view){
        if ( getItem(0) < 3){
            xslideViewPager.setCurrentItem(getItem(1),true);
        }else{
            Intent i = new Intent(OnBoarding.this, LogIn.class);
            startActivity(i);
            finish();
        }
    }

    public void onSkipPressed(View view){
        Intent i = new Intent(OnBoarding.this, LogIn.class);
        startActivity(i);
        finish();
    }

    private void checkFirstOpen(){
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            Intent intent = new Intent(OnBoarding.this, LogIn.class);
            startActivity(intent);
            finish();

        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun",
                false).apply();
    }
}