package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;

public class Help extends AppCompatActivity {

    ImageView helpBackIv;
    RecyclerView helpRecycler;
    ArrayList<Faqs> faqsArrayList;
    String[] helpHeadings;
    String[] helpBodies;
    FaqsAdapter faqsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().hide();
        //to adopt status bar to the pink header
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.pink));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        helpBackIv = (ImageView) findViewById(R.id.helpBackIv);
        helpRecycler = (RecyclerView) findViewById(R.id.helpRecycler);

        helpRecycler = (RecyclerView) findViewById(R.id.helpRecycler);
        helpRecycler.setLayoutManager(new LinearLayoutManager(this));
        helpRecycler.setHasFixedSize(true);

        faqsArrayList = new ArrayList<Faqs>();

        helpHeadings = new String[]{
                "Are the pets up for adoption in good health?",
                "Is there a cost involved?",
                "What is the maximum number of pets that can be adopted?"
        };

        helpBodies = new String[]{
                getString(R.string.body1),
                getString(R.string.body2),
                getString(R.string.body3)
        };

        getData();
    }

    public void getData() {
        for (int i = 0; i < helpHeadings.length; i++) {
            Faqs faqs = new Faqs(helpHeadings[i], helpBodies[i]);
            faqsArrayList.add(faqs);
        }

        faqsAdapter = new FaqsAdapter(this, faqsArrayList);
        helpRecycler.setAdapter(faqsAdapter);
        faqsAdapter.notifyDataSetChanged();
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