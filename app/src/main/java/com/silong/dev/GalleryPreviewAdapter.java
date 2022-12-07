package com.silong.dev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Operation.Utility;

import java.util.ArrayList;

public class GalleryPreviewAdapter extends RecyclerView.Adapter<GalleryPreviewAdapter.ViewHolder> {

    private ArrayList<Bitmap> images;
    private Activity activity;

    public GalleryPreviewAdapter(Activity activity, ArrayList<Bitmap> images){
        this.activity = activity;
        this.images = images;
    }

    @NonNull
    @Override
    public GalleryPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_preview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryPreviewAdapter.ViewHolder holder, int position) {
        holder.galleryPreviewPic.setImageBitmap(images.get(position));

        holder.galleryPreviewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.animateOnClick(activity, view);
                Intent i = new Intent(activity, Homepage.class);
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                activity.finish();

            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryPreviewPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryPreviewPic = itemView.findViewById(R.id.galleryPreviewPic);
        }
    }
}
