package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yalantis.library.Koloda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    /* APP-SPECIFIC FILES */
    protected final UserData userData = new UserData();
    protected final File USERDATA = new File(getFilesDir(),"user.dat");
    protected final File AVATARDATA = new File(getFilesDir(),userData.getUserID() + "avatar.dat");
    protected final File ADOPTIONDATA = new File(getFilesDir(),"adoption.dat");
    protected final File CHATDATA = new File(getFilesDir(),"chat.dat");
    protected final File CHATCONFIG = new File(getFilesDir(),"chat.config");
    protected final File PETDATA = new File(getFilesDir(),"pet.dat");
    protected final File FAVORITECONFIG = new File(getFilesDir(),"favorite.config");

    private SwipeAdapter adapter;
    private List<Integer> list;
    Koloda koloda;

    TextView headerTitle;
    ImageView filterImgview, messageImgview, menuImgview;
    Button applyBtn;
    ImageView heartIcon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getSupportActionBar().hide();

        View view = findViewById(R.id.headerLayout);
        headerTitle = (TextView) findViewById(R.id.headerTitle);
        filterImgview = (ImageView) findViewById(R.id.filterImgview);
        messageImgview = (ImageView) findViewById(R.id.messageImgview);
        menuImgview = (ImageView) findViewById(R.id.menuImgview);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        heartIcon = (ImageView) findViewById(R.id.heartIcon);
        filterImgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDia(Homepage.this);
            }
        });

        //Koloda swipe
        koloda = findViewById(R.id.koloda);
        list = new ArrayList<>();

        adapter = new SwipeAdapter(this, list);
        koloda.setAdapter(adapter);
    }
    //Method for executing Filter Dialog
    public void filterDia(Context context){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setBackground(getDrawable(R.drawable.dialog_bg));
        builder.setView(R.layout.filter_layout);
        builder.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Codes here
            }
        });
        builder.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
        builder.show();
    }
}