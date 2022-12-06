package com.silong.dev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryPreviewAdapter extends RecyclerView.Adapter<GalleryPreviewAdapter.ViewHolder> {

    ArrayList<GalleryPreviewModel> galleryPreviewModels;
    Context context;

    public GalleryPreviewAdapter(Context context, ArrayList<GalleryPreviewModel> galleryPreviewModels){
        this.context = context;
        this.galleryPreviewModels = galleryPreviewModels;
    }

    @NonNull
    @Override
    public GalleryPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_preview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryPreviewAdapter.ViewHolder holder, int position) {
        holder.galleryPreviewPic.setImageResource(galleryPreviewModels.get(position).getGalleryPreview());
    }

    @Override
    public int getItemCount() {
        return galleryPreviewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView galleryPreviewPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryPreviewPic = itemView.findViewById(R.id.galleryPreviewPic);
        }
    }
}
