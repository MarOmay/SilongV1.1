package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class AdoptionHistory extends AppCompatActivity {

    ImageView historyBackIv;
    RecyclerView historyRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_history);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        historyBackIv = (ImageView) findViewById(R.id.historyBackIv);
        historyRecycler = (RecyclerView) findViewById(R.id.historyRecycler);

        historyRecycler.setHasFixedSize(true);
        historyRecycler.setLayoutManager(new LinearLayoutManager(AdoptionHistory.this));

        //Array for accounts
        HistoryData[] historyData = new HistoryData[]{
                new HistoryData(R.drawable.dog_cat, "Gender Type", "Age", "Color", "Size", "Month XX, XXXX", "STATUS"),
                new HistoryData(R.drawable.dog_cat, "Gender Type", "Age", "Color", "Size", "Month XX, XXXX", "STATUS"),
                new HistoryData(R.drawable.dog_cat, "Gender Type", "Age", "Color", "Size", "Month XX, XXXX", "STATUS")
        };

        HistoryAdapter historyAdapter = new HistoryAdapter(historyData, AdoptionHistory.this);
        historyRecycler.setAdapter(historyAdapter);
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}