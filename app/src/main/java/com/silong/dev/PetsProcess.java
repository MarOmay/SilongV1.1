package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.silong.Adapter.HistoryAdapter;
import com.silong.Adapter.ProcessAdapter;

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

        ProcessData [] processData = new ProcessData[] {
                new ProcessData("Female Dog", R.drawable.silong_user_app_icon),
                new ProcessData("Male Cat", R.drawable.silong_user_app_icon),
                new ProcessData("Female Cat", R.drawable.silong_user_app_icon),
                new ProcessData("Male Dog", R.drawable.silong_user_app_icon)
        };

        ProcessAdapter processAdapter = new ProcessAdapter(processData, PetsProcess.this);
        petsProcessRecycler.setAdapter(processAdapter);

    }

    public void back (View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}