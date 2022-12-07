package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silong.Adapter.ProofPagerAdapter;
import com.silong.Adapter.ViewPagerAdapter;

public class ProofAdoption extends AppCompatActivity {

    ViewPager proofSlidePager;
    LinearLayout dotIndicatorLayout;

    TextView[] xdots;
    ProofPagerAdapter proofPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proof_adoption);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        proofSlidePager = (ViewPager) findViewById(R.id.slideViewPager);
        dotIndicatorLayout = (LinearLayout) findViewById(R.id.proofIndicatorLayout);

        proofPagerAdapter = new ProofPagerAdapter(ProofAdoption.this);
        proofSlidePager.setAdapter(proofPagerAdapter);
        setUpIndicator(0);
        proofSlidePager.addOnPageChangeListener(viewListener);
    }

    public void setUpIndicator(int position) {
        xdots = new TextView[proofPagerAdapter.getCount()];
        dotIndicatorLayout.removeAllViews();

        for (int i = 0; i < xdots.length; i++) {
            xdots[i] = new TextView(this);
            xdots[i].setText(Html.fromHtml("&#8226"));
            xdots[i].setTextSize(35);
            xdots[i].setTextColor(getResources().getColor(R.color.lightgray));
            dotIndicatorLayout.addView(xdots[i]);
        }

        xdots[position].setTextColor(getResources().getColor(R.color.pink));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setUpIndicator(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
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