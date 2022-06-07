package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class Homepage extends AppCompatActivity {

    /* APP-SPECIFIC FILES */
    protected final File USERDATA = new File(getFilesDir(),"user.dat");
    protected final File AVATARDATA = new File(getFilesDir(),"avatar.dat");
    protected final File ADOPTIONDATA = new File(getFilesDir(),"adoption.dat");
    protected final File CHATDATA = new File(getFilesDir(),"chat.dat");
    protected final File CHATCONFIG = new File(getFilesDir(),"chat.config");
    protected final File PETDATA = new File(getFilesDir(),"pet.dat");
    protected final File FAVORITECONFIG = new File(getFilesDir(),"favorite.config");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }
}