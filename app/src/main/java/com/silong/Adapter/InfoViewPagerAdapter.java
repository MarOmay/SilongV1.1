package com.silong.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.silong.dev.R;

import java.util.ArrayList;

public class InfoViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Bitmap> photos;

    public InfoViewPagerAdapter (Context context, ArrayList<Bitmap> photos){
        this.context = context;
        this.photos = photos;
    }

    @Override
    public int getCount() { return photos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.info_slider_layout, container, false);

        ImageView sliderImage = (ImageView) view.findViewById(R.id.infoSliderImage);

        sliderImage.setImageBitmap(photos.get(position));
        sliderImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

}
