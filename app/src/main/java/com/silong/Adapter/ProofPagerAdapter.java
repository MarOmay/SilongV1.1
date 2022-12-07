package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.silong.CustomView.ImageDialog;
import com.silong.Object.ProofOfAdoption;
import com.silong.Operation.Utility;
import com.silong.dev.ProofAdoption;
import com.silong.dev.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProofPagerAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<ProofOfAdoption> proof;

    public ProofPagerAdapter(Activity activity, ArrayList<ProofOfAdoption> proof){
        this.activity = activity;
        this.proof = proof;
    }

    @Override
    public int getCount() {
        return proof.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.proof_slider_layout, container, false);

        ImageView proofCertPic = (ImageView) view.findViewById(R.id.proofCertPic);
        ImageView proofPetPic = (ImageView) view.findViewById(R.id.proofPetPic);
        TextView proofTypeTv = (TextView) view.findViewById(R.id.proofTypeTv);
        TextView proofDateTv = (TextView) view.findViewById(R.id.proofDateTv);

        proofCertPic.setImageBitmap(proof.get(position).getProofOfAdoption());
        proofPetPic.setImageBitmap(proof.get(position).getPetPhoto());
        proofTypeTv.setText(proof.get(position).getGenderType());
        proofDateTv.setText(proof.get(position).getDateOfAdoption());

        proofCertPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog imageDialog = new ImageDialog(activity, proofCertPic.getDrawable());
                imageDialog.show();
            }
        });

        proofPetPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDialog imageDialog = new ImageDialog(activity, proofPetPic.getDrawable());
                imageDialog.show();
            }
        });

        ImageView proofDownload = view.findViewById(R.id.proofDownload);

        proofDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.requestPermission(activity, Utility.STORAGE_REQUEST_CODE))
                    return;

                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/Silong/";

                File dir = new File( path);

                if (!dir.exists()){
                    dir.mkdirs();
                }

                String filename = path + "proofOfAdoption-"+System.currentTimeMillis()+".jpg";

                try (FileOutputStream out = new FileOutputStream(filename)) {
                    proof.get(position).getProofOfAdoption().compress(Bitmap.CompressFormat.PNG, 100, out);
                    Toast.makeText(activity, "Image saved", Toast.LENGTH_SHORT).show();

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File(filename));
                    mediaScanIntent.setData(contentUri);
                    ContextWrapper cw = new ContextWrapper(activity);
                    cw.sendBroadcast(mediaScanIntent);
                }
                catch (Exception e){
                    Toast.makeText(activity, "Can't save image.", Toast.LENGTH_SHORT).show();
                    Utility.log("PPA.oPS: " + e.getMessage());
                }
            }
        });


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
