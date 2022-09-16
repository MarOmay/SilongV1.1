package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class TermsOfUse extends AppCompatActivity {

    LinearLayout termsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);
        getSupportActionBar().hide();

        termsLayout = findViewById(R.id.termsLayout);

        int termsHeading[] = {
                R.string.tncHead1, R.string.tncHead2,
                R.string.tncHead3, R.string.tncHead4,
                R.string.tncHead5, R.string.tncHead6,
                R.string.tncHead7, R.string.tncHead8,
                R.string.tncHead9, R.string.tncHead10,
                R.string.tncHead11, R.string.tncHead12,
                R.string.tncHead13, R.string.tncHead14,
                R.string.tncHead15, R.string.tncHead16,
                R.string.tncHead17, R.string.tncHead18,
                R.string.tncHead19, R.string.tncHead20,
                R.string.tncHead21, R.string.tncHead22
        };

        int termsBody[] = {
                R.string.tncBody1, R.string.tncBody2,
                R.string.tncBody3, R.string.tncBody4,
                R.string.tncBody5, R.string.tncBody6,
                R.string.tncBody7, R.string.tncBody8,
                R.string.tncBody9, R.string.tncBody10,
                R.string.tncBody11, R.string.tncBody12,
                R.string.tncBody13, R.string.tncBody14,
                R.string.tncBody15, R.string.tncBody16,
                R.string.tncBody17, R.string.tncBody18,
                R.string.tncBody19, R.string.tncBody20,
                R.string.tncBody21, R.string.tncBody22
        };

        for ( int i = 0 ; i < termsHeading.length ; i++){
            TextView textHeading = new TextView(this);
            textHeading.setTextSize(20);
            textHeading.setTextColor(getResources().getColor(R.color.black));
            textHeading.setPadding(0,0,0,20);
            textHeading.setTypeface(textHeading.getTypeface(), Typeface.BOLD);
            textHeading.setText(termsHeading[i]);

            TextView textBody = new TextView(this);
            textBody.setTextSize(15);
            textBody.setTextColor(getResources().getColor(R.color.darkgray));
            textBody.setText(termsBody[i]);

            termsLayout.addView(textHeading);
            termsLayout.addView(textBody);
        }

    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}