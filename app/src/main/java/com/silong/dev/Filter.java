package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class Filter extends AppCompatActivity {

    private Chip dogChip, catChip, puppyChip, youngChip, oldChip, maleChip, femaleChip;
    private ChipGroup genderToggle, ageToggle, typeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().hide();

        //initialize chips and group
        typeToggle = findViewById(R.id.typeToggle);
        ageToggle = findViewById(R.id.ageToggle);
        genderToggle = findViewById(R.id.genderToggle);

        dogChip = findViewById(R.id.dogChip);
        catChip = findViewById(R.id.catChip);
        puppyChip = findViewById(R.id.puppyChip);
        youngChip = findViewById(R.id.youngChip);
        oldChip = findViewById(R.id.oldChip);
        maleChip = findViewById(R.id.maleChip);
        femaleChip = findViewById(R.id.femaleChip);

        if (Homepage.DOG) typeToggle.check(R.id.dogChip);
        if (Homepage.CAT) typeToggle.check(R.id.catChip);
        if (Homepage.PUPPY) ageToggle.check(R.id.puppyChip);
        if (Homepage.YOUNG) ageToggle.check(R.id.youngChip);
        if (Homepage.OLD) ageToggle.check(R.id.oldChip);
        if (Homepage.MALE) genderToggle.check(R.id.maleChip);
        if (Homepage.FEMALE) genderToggle.check(R.id.femaleChip);

    }

    public void onPressedDog(View view){
        Homepage.DOG = dogChip.isChecked();
    }

    public void onPressedCat(View view){
        Homepage.CAT = catChip.isChecked();
    }

    public void onPressedPuppy(View view){
        Homepage.PUPPY = puppyChip.isChecked();
    }

    public void onPressedYoung(View view){
        Homepage.YOUNG = youngChip.isChecked();
    }

    public void onPressedOld(View view){
        Homepage.OLD = oldChip.isChecked();
    }

    public void onPressedMale(View view){
        Homepage.MALE = maleChip.isChecked();
    }

    public void onPressedFemale(View view){
        Homepage.FEMALE = femaleChip.isChecked();
    }

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Filter.this, Homepage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


}