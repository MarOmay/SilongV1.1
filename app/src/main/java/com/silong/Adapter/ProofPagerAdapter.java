package com.silong.Adapter;

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

import com.silong.dev.R;

public class ProofPagerAdapter extends PagerAdapter {

    Context context;

    int proofcertPic[] = { R.drawable.silong_user_app_icon, R.drawable.silong_user_app_icon};

    int proofpetPic[] = { R.drawable.silong_user_app_icon, R.drawable.silong_user_app_icon };

    String[] proofpetType = { "Female Cat" , "Male Dog"};

    String[] proofdateAdopt = { "02-03-2004" , "11-11-2011"};

    public ProofPagerAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return proofpetType.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.proof_slider_layout, container, false);

        ImageView proofCertPic = (ImageView) view.findViewById(R.id.proofCertPic);
        ImageView proofPetPic = (ImageView) view.findViewById(R.id.proofPetPic);
        TextView proofTypeTv = (TextView) view.findViewById(R.id.proofTypeTv);
        TextView proofDateTv = (TextView) view.findViewById(R.id.proofDateTv);

        proofCertPic.setImageResource(proofcertPic[position]);
        proofPetPic.setImageResource(proofpetPic[position]);
        proofTypeTv.setText(proofpetType[position]);
        proofDateTv.setText(proofdateAdopt[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
