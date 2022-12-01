package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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

        if (Homepage.CAT) typeToggle.check(R.id.catChip);
        if (Homepage.DOG) typeToggle.check(R.id.dogChip);
        if (Homepage.PUPPY) ageToggle.check(R.id.puppyChip);
        if (Homepage.YOUNG) ageToggle.check(R.id.youngChip);
        if (Homepage.OLD) ageToggle.check(R.id.oldChip);
        if (Homepage.MALE) genderToggle.check(R.id.maleChip);
        if (Homepage.FEMALE) genderToggle.check(R.id.femaleChip);

        if (Homepage.CAT && Homepage.DOG){
            Homepage.CAT = false;
        }

    }

    public void onPressedResetFilter(View view){
        Homepage.DOG = true;
        Homepage.CAT = true;
        Homepage.PUPPY = true;
        Homepage.YOUNG = true;
        Homepage.OLD = true;
        Homepage.MALE = true;
        Homepage.FEMALE = true;

        onBackPressed();
    }

    public void onPressedDog(View view){
        Homepage.DOG = dogChip.isChecked();
        Homepage.CAT = false;

        puppyChip.setText("PUPPY");
        catChip.setSelected(false);
    }

    public void onPressedCat(View view){
        Homepage.CAT = catChip.isChecked();
        Homepage.DOG = false;

        puppyChip.setText("KITTEN");
        dogChip.setSelected(false);
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