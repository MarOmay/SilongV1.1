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

public class ViewPagerAdapter extends PagerAdapter {

    Context context;

    int images[] = { R.drawable.onboarding_1, R.drawable.onboarding_2, R.drawable.onboarding_3 };

    int description[] = { R.string.onboarding1, R.string.onboarding2, R.string.onboarding3 };

    public ViewPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return description.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView sliderImage = (ImageView) view.findViewById(R.id.sliderImage);
        TextView onBoardDescTv = (TextView) view.findViewById(R.id.onBoardDescTv);

        sliderImage.setImageResource(images[position]);
        onBoardDescTv.setText(description[position]);
        onBoardDescTv.setTextSize(18);

        //for Mabuhay Only
        if (position == 0){
            onBoardDescTv.setTextSize(30);
            onBoardDescTv.setTypeface(onBoardDescTv.getTypeface(), Typeface.BOLD);
            TextView swipeTv = (TextView) view.findViewById(R.id.swipeTv);
            swipeTv.setText("Swipe screens to navigate.");
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
