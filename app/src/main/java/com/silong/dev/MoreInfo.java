package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreInfo extends AppCompatActivity {

    ViewPager moreInfoVp;
    LinearLayout moreInfoIndicator;

    TextView[] dots;
    InfoViewPagerAdapter infoViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        getSupportActionBar().hide();

        moreInfoVp = findViewById(R.id.moreInfoVp);
        moreInfoIndicator = findViewById(R.id.moreInfoIndicator);

        infoViewPagerAdapter = new InfoViewPagerAdapter(MoreInfo.this);
        moreInfoVp.setAdapter(infoViewPagerAdapter);
        setUpIndicator(0);
        moreInfoVp.addOnPageChangeListener(viewListener);

    }

    public void setUpIndicator(int position) {
        dots = new TextView[3];
        moreInfoIndicator.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.lightgray));
            moreInfoIndicator.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.pink));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position) { setUpIndicator(position); }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}