package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.silong.Adapter.HistoryAdapter;
import com.silong.Adapter.ProcessAdapter;
import com.silong.Object.Pet;

public class PetsProcess extends AppCompatActivity {

    ImageView petsProcessBackIv;
    RecyclerView petsProcessRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pets_process);
        getSupportActionBar().hide();

        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        petsProcessBackIv = (ImageView) findViewById(R.id.petsProcessBackIv);
        petsProcessRecycler = (RecyclerView) findViewById(R.id.petsProcessRecycler);

        petsProcessRecycler.setHasFixedSize(true);
        petsProcessRecycler.setLayoutManager(new LinearLayoutManager(PetsProcess.this));

        ProcessAdapter processAdapter = new ProcessAdapter(UserData.getLocalPetsInprocess(PetsProcess.this), PetsProcess.this);
        petsProcessRecycler.setAdapter(processAdapter);

    }

    public void back (View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PetsProcess.this, LandingPage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}