package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.silong.Adapter.ProofPagerAdapter;
import com.silong.Adapter.ViewPagerAdapter;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;
import com.silong.Object.ProofOfAdoption;
import com.silong.Operation.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

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

        prepareData();
    }

    private void prepareData(){

        ArrayList<ProofOfAdoption> proof = new ArrayList<>();

        for (File file : getFilesDir().listFiles()){
            String path = file.getAbsolutePath();
            if (path.contains("poa-")){
                try {
                    ProofOfAdoption poa = new ProofOfAdoption();

                    Bitmap proofOfAdoption = BitmapFactory.decodeFile(path);
                    String petID = path.split("poa-")[1];

                    Adoption adoption = new Adoption();

                    UserData.populateAdoptions(ProofAdoption.this);
                    for (Adoption adp : UserData.adoptionHistory){
                        if (adp.getPetID().equals(petID)){
                            adoption = adp;
                        }
                    }

                    //translate gender and type
                    String genderType = "";
                    switch (adoption.getGender()){
                        case Gender.MALE: genderType = "Male "; break;
                        case Gender.FEMALE: genderType = "Female "; break;
                    }
                    switch (adoption.getType()){
                        case PetType.DOG: genderType += "Dog"; break;
                        case PetType.CAT: genderType += "Cat"; break;
                    }

                    poa.setProofOfAdoption(proofOfAdoption);
                    poa.setDateOfAdoption(adoption.getDateRequested());
                    poa.setGenderType(genderType);
                    poa.setPetPhoto(adoption.getPhoto());

                    proof.add(poa);
                }
                catch (Exception e){
                    Utility.log("ProofAdoption.pD: " + e.getMessage());
                }

            }
        }

        proofPagerAdapter = new ProofPagerAdapter(ProofAdoption.this, proof);
        proofSlidePager.setAdapter(proofPagerAdapter);
        setUpIndicator(0);
        proofSlidePager.addOnPageChangeListener(viewListener);
    }

    public void setUpIndicator(int position) {
        if (proofPagerAdapter.getCount() <= 0){
            return;
        }

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