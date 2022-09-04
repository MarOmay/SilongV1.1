package com.silong.dev;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.collection.LLRBNode;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;

import java.util.ArrayList;
import java.util.List;

public class SwipeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Pet> pets;

    public SwipeAdapter(Context context, ArrayList<Pet> pets) {
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

        try{
            ImageView imageSwipe = convertView.findViewById(R.id.imageSwipe);
            ImageView genderSign = convertView.findViewById(R.id.genderSign);
            ImageView animalFace = convertView.findViewById(R.id.animalFace);
            TextView petColor = convertView.findViewById(R.id.petColor);

            Pet p = pets.get(index);

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
}
