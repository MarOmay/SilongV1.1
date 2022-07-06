package com.silong.dev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.silong.Operation.ImagePicker;
import com.silong.Operation.ImageProcessor;
import com.silong.Operation.InternetConnection;

import java.io.BufferedInputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    public final static int READ_EXTERNAL_STORAGE_REQUEST = 0;
    private static final int PICK_IMAGE = 2;

    ImageView imageView, preview;
    Button button, upload;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Test Commit

        imageView = findViewById(R.id.imageView);
        preview = findViewById(R.id.preview);

        File file = new File(MainActivity.this.getFilesDir(),"test-pic-1.jpeg");

        Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());
        imageView.setImageBitmap(bm);

        if (file.exists()){
            Toast.makeText(this, "YES", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "NO", Toast.LENGTH_SHORT).show();
        }

        tv = findViewById(R.id.text);

        button = findViewById(R.id.b1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImagePicker(MainActivity.this, PICK_IMAGE);
            }
        });

        upload = findViewById(R.id.b2);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Try retrieve", Toast.LENGTH_SHORT).show();
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                if (!new ImageProcessor().checkFileSize(bitmap, true)){
                    Toast.makeText(MainActivity.this, "Exceed Limit", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Toast.makeText(MainActivity.this, "Try convert", Toast.LENGTH_SHORT).show();
                String utf8 = new ImageProcessor().toUTF8(bitmap, true);
                //Toast.makeText(MainActivity.this, "Size: " + utf8.length(), Toast.LENGTH_SHORT).show();
                bitmap = new ImageProcessor().toBitmap(utf8);
                //Toast.makeText(MainActivity.this, "Try set", Toast.LENGTH_SHORT).show();
                preview.setImageBitmap(bitmap);
                tv.setText(utf8);

                Toast.makeText(MainActivity.this, "Try save", Toast.LENGTH_SHORT).show();
                if (!new ImageProcessor().saveToLocal(MainActivity.this, bitmap, "test-pic-1.jpeg")){
                    tv.setText("Failed");
                }else {
                    tv.setText("Saved");
                }

                String[] files = MainActivity.this.fileList();
                for (String f : files) {
                    tv.setText(tv.getText()+"\n"+f);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            try{
                BufferedInputStream bufferedInputStream = new BufferedInputStream(getContentResolver().openInputStream(data.getData()));
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                imageView.setImageBitmap(bmp);            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to choose file", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Allowed
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}