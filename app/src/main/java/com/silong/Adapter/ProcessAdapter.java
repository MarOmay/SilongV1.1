package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.ProcessDialog;
import com.silong.EnumClass.Gender;
import com.silong.EnumClass.PetAge;
import com.silong.EnumClass.PetColor;
import com.silong.EnumClass.PetSize;
import com.silong.EnumClass.PetType;
import com.silong.Object.Pet;
import com.silong.dev.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ViewHolder>{

    private Activity activity;
    private ArrayList<Pet> pets;
    Context context;

    public ProcessAdapter(ArrayList<Pet> pets, Activity activity){
        this.pets = pets;
        this.context = activity;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.process_list_layout, parent, false);
        ViewHolder viewHolder = new ProcessAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessAdapter.ViewHolder holder, int position) {
        final Pet pet = pets.get(position);

        //translate gender and type
        String genderType = "";
        switch (pet.getGender()){
            case Gender.MALE: genderType = "Male "; break;
            case Gender.FEMALE: genderType = "Female "; break;
        }
        switch (pet.getType()){
            case PetType.DOG: genderType += "Dog"; break;
            case PetType.CAT: genderType += "Cat"; break;
        }

        //translate age
        String age = "";
        switch (pet.getAge()){
            case PetAge.PUPPY: age = (pet.getType() == PetType.DOG ? "Puppy" : "Kitten"); break;
            case PetAge.YOUNG: age = "Young"; break;
            case PetAge.OLD: age = "Adult"; break;
        }

        //translate color
        String color = "";
        for (char c : pet.getColor().toCharArray()){
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

        holder.processImage.setImageBitmap(pet.getPhoto());
        holder.processGenderType.setText(genderType);

        final String gType = genderType;
        final String colors = color;
        final String pAge = age;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<>();
                map.put("petID", pet.getPetID());
                map.put("genderType", gType);
                map.put("color", colors);
                map.put("age", pAge);
                map.put("pic", pet.getPhoto());

                ProcessDialog processDialog = new ProcessDialog(activity, map);
                processDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView processImage;
        TextView processGenderType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            processImage = itemView.findViewById(R.id.processImage);
            processGenderType = itemView.findViewById(R.id.processGenderType);
        }
    }
}
