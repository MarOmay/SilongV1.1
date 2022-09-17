package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Adoption;

import java.util.Comparator;

public class AdoptionHistory extends AppCompatActivity {

    ImageView historyBackIv;
    RecyclerView historyRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_history);
        getSupportActionBar().hide();

        UserData.populateAdoptions(AdoptionHistory.this);

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        historyBackIv = (ImageView) findViewById(R.id.historyBackIv);
        historyRecycler = (RecyclerView) findViewById(R.id.historyRecycler);

        historyRecycler.setHasFixedSize(true);
        historyRecycler.setLayoutManager(new LinearLayoutManager(AdoptionHistory.this));

        loadAdoptionList();
    }

    private void loadAdoptionList(){

        try {
            // sort by dateRequested
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UserData.adoptionHistory.sort(new Comparator<Adoption>() {
                    @Override
                    public int compare(Adoption adoption, Adoption t1) {
                        return adoption.getDateRequested().compareTo(t1.getDateRequested());
                    }
                });
            }
        }
        catch (Exception e){
            Log.d("AdopH-lAL", "Failed to sort adoptionHistory");
        }

        try {

            HistoryData [] historyData = new HistoryData[UserData.adoptionHistory.size()];

            for (Adoption adoption : UserData.adoptionHistory){

                Log.d("DEBUGGER>>>", "AH " + adoption.getDateRequested());

                //translate gender and type
                String genderType = "";
                switch (adoption.getGender()){
                    case Gender.MALE: genderType = "Male"; break;
                    case Gender.FEMALE: genderType = "Female"; break;
                }
                switch (adoption.getType()){
                    case PetType.DOG: genderType += " Dog"; break;
                    case PetType.CAT: genderType += " Cat"; break;
                }

                //translate age
                String age = "";
                switch (adoption.getAge()){
                    case PetAge.PUPPY: age = (adoption.getType() == PetType.DOG ? "Puppy" : "Kitten"); break;
                    case PetAge.YOUNG: age = "Young"; break;
                    case PetAge.OLD: age = "Old"; break;
                }

                //translate color
                String color = "";
                for (char c : adoption.getColor().toCharArray()){
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
                switch (adoption.getSize()){
                    case PetSize.SMALL: size = "Small"; break;
                    case PetSize.MEDIUM: size = "Medium"; break;
                    case PetSize.LARGE: size = "Large"; break;
                }

                historyData[UserData.adoptionHistory.indexOf(adoption)] = new HistoryData(adoption.getPhoto(), genderType, age, color, size, adoption.getDateRequested(), adoption.getStatus());
            }

            HistoryAdapter historyAdapter = new HistoryAdapter(historyData, AdoptionHistory.this);
            historyRecycler.setAdapter(historyAdapter);

        }
        catch (Exception e){
            Toast.makeText(this, "Can't load adoption history.", Toast.LENGTH_SHORT).show();
            Log.d("DEBUGGER>>>","@AdoptionHistory-loadAdoptionList: " + e.getMessage());
        }

    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}