package com.silong.dev;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class InfoViewPagerAdapter extends PagerAdapter {

    Context context;
    int images[] = { R.drawable.onboarding_1, R.drawable.onboarding_2_2, R.drawable.onboarding_3 };

    public InfoViewPagerAdapter (Context context){
        this.context = context;
    }

    @Override
    public int getCount() { return images.length;
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

        sliderImage.setImageResource(images[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
