package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silong.Adapter.InfoViewPagerAdapter;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;

public class MoreInfo extends AppCompatActivity {

    ViewPager moreInfoVp;
    LinearLayout moreInfoIndicator;

    TextView[] dots;
    InfoViewPagerAdapter infoViewPagerAdapter;

    private TextView moreType, moreGender, moreAge, moreSize, moreColor, moreRescueDate;
    private TextView morePetId;

    private Pet PET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        getSupportActionBar().hide();

        moreInfoVp = findViewById(R.id.moreInfoVp);
        moreInfoIndicator = findViewById(R.id.moreInfoIndicator);
        moreType = findViewById(R.id.moreType);
        moreGender = findViewById(R.id.moreGender);
        moreAge = findViewById(R.id.moreAge);
        moreSize = findViewById(R.id.moreSize);
        moreColor = findViewById(R.id.moreColor);
        moreRescueDate = findViewById(R.id.moreRescueDate);
        morePetId = findViewById(R.id.morePetId);

        loadPetInfo();

    }

    private void loadPetInfo(){
        try {
            String petID = getIntent().getSerializableExtra("petID").toString();
            PET = UserData.getPet(petID);

            //translate gender and type
            String gender = "";
            switch (PET.getGender()){
                case Gender.MALE: gender = "Male"; break;
                case Gender.FEMALE: gender = "Female"; break;
            }

            String type = "";
            switch (PET.getType()){
                case PetType.DOG: type = "Dog"; break;
                case PetType.CAT: type = "Cat"; break;
            }

            //translate age
            String age = "";
            switch (PET.getAge()){
                case PetAge.PUPPY: age = (PET.getType() == PetType.DOG ? "Puppy" : "Kitten"); break;
                case PetAge.YOUNG: age = "Young"; break;
                case PetAge.OLD: age = "Old"; break;
            }

            //translate color
            String color = "";
            for (char c : PET.getColor().toCharArray()){
                switch (Integer.parseInt(c+"")){
                    case PetColor.BLACK: color += "Black "; break;
                    case PetColor.BROWN: color += "Brown "; break;
                    case PetColor.CREAM: color += "Cream "; break;
                    case PetColor.WHITE: color += "White "; break;
                    case PetColor.ORANGE: color += "Orange "; break;
                    case PetColor.GRAY: color += "Gray "; break;
                }
            }
            color.trim();
            color.replace(" ", " / ");

            //translate size
            String size = "";
            switch (PET.getSize()){
                case PetSize.SMALL: size = "Small"; break;
                case PetSize.MEDIUM: size = "Medium"; break;
                case PetSize.LARGE: size = "Large"; break;
            }

            //update ui
            moreType.setText(type);
            moreGender.setText(gender);
            moreAge.setText(age);
            moreSize.setText(size);
            moreColor.setText(color);
            moreRescueDate.setText("Data not available");

            Bitmap[] images = new Bitmap[]{PET.getPhoto()};

            infoViewPagerAdapter = new InfoViewPagerAdapter(MoreInfo.this, images);
            moreInfoVp.setAdapter(infoViewPagerAdapter);
            setUpIndicator(images.length);
            moreInfoVp.addOnPageChangeListener(viewListener);
        }
        catch (Exception e){
            Utility.log("MoreInfo.lPI: " + e.getMessage());
        }
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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

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