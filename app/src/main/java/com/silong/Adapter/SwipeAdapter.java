package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.Operation.Utility;
import com.silong.dev.Homepage;
import com.silong.dev.HorizontalProgressBar;
import com.silong.dev.R;
import com.silong.dev.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SwipeAdapter extends BaseAdapter {

    private Activity activity;
    private Context context;
    private ArrayList<Pet> pets;
    private Map<String, Integer> petIndexer = new HashMap<>();
    private int indexer = 1;

    private SwipeRefreshLayout swipeRefreshLayout;

    public SwipeAdapter(Activity activity, ArrayList<Pet> pets) {
        this.activity = activity;
        this.context = context;
        this.pets = pets;
    }

    @Override
    public int getCount() {
        return pets.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int index = position;
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_koloda, parent, false);
        }

        swipeRefreshLayout = convertView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        try{
            ImageView imageSwipe = convertView.findViewById(R.id.imageSwipe);
            ImageView genderSign = convertView.findViewById(R.id.genderSign);
            ImageView animalFace = convertView.findViewById(R.id.animalFace);
            TextView petColor = convertView.findViewById(R.id.petColor);

            Pet p = pets.get(index);

            if (!petIndexer.containsKey(p.getPetID()))
                petIndexer.put(p.getPetID(), indexer++);

            imageSwipe.setImageBitmap(p.getPhoto());
            genderSign.setImageResource(p.getGender() == Gender.MALE ? R.drawable.gendermale : R.drawable.genderfemale);
            animalFace.setImageResource(p.getType() == PetType.DOG ? R.drawable.dogface : R.drawable.catface);

            //translate color
            String color = "";
            for (char c : p.getColor().toCharArray()){
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

            petColor.setText(color);

        }
        catch (Exception e){
            Log.d("SwipeAdapter-gV", e.getMessage());
        }


        return convertView;
    }

    private void attachLikeListener(ImageView imageView, Pet pet){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pet.setLiked(!pet.isLiked());
                UserData.writePetToLocal(context, pet.getPetID(), "liked", "" + pet.isLiked());
                UserData.pets.get(UserData.pets.indexOf(pet)).setLiked(pet.isLiked());

                if (pet.isLiked()){
                    imageView.setColorFilter(ContextCompat.getColor(context, R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                else {
                    imageView.clearColorFilter();
                }
            }
        });
    }

    public void insert(Pet p){
        pets.add(p);
    }

    public ArrayList<Pet> getList(){
        return pets;
    }

    public int getPetIndex(String petID){
        return petIndexer.get(petID);
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            Utility.log("SwipeAdapter: triggered");

            Intent intent = new Intent(activity, HorizontalProgressBar.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            swipeRefreshLayout.setRefreshing(false);
            activity.finish();

        }
    };
}
