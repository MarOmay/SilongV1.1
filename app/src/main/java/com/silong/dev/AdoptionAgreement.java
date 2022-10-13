package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdoptionAgreement extends AppCompatActivity {

    LinearLayout conditionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_agreement);
        getSupportActionBar().hide();

        conditionsLayout = findViewById(R.id.conditionsLayout);

        TextView conditionsTitle = new TextView(this);
        conditionsTitle.setText(R.string.adoptionConditions1);
        conditionsTitle.setTextSize(18);
        conditionsTitle.setTextColor(getResources().getColor(R.color.black));

        TextView conditionsBody = new TextView(this);
        conditionsBody.setText(R.string.adoptionConditions2);
        conditionsBody.setTextSize(16);
        conditionsBody.setTextColor(getResources().getColor(R.color.black));

        conditionsLayout.addView(conditionsTitle);
        conditionsLayout.addView(conditionsBody);
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