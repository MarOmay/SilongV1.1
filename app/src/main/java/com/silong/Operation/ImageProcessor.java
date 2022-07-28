package com.silong.Operation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;

public class ImageProcessor {

    //Maximum file size allowed [MB] * [KB] * [B]
    private final int FILE_LIMIT_IN_KB = 1 * 1024 * 1024;
    private final int COMPRESSION = 50;

    public ImageProcessor(){
        /* toUTF8 - Bitmap to Base64 to UTF* conversion
        *  toBitmap - UTF8/Base64 to Bitmap
        *  checkFileSize - compare FILE_LIMIT_IN_KB vs actual byte size of bitmap
        * */

    }

    public boolean checkFileSize(Drawable drawable, boolean compress){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return checkFileSize(bitmap, compress);
    }

    public boolean checkFileSize(Bitmap bitmap, boolean compress){
        /* Check the file size in kb
        *   returns true if file size < FILE_LIMIT_IN_KB
        *   returns false otherwise */

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress ? COMPRESSION : 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long length = imageInByte.length;

        return length < FILE_LIMIT_IN_KB;
    }

    public String toUTF8(Bitmap bitmap, boolean compress){
        /* Converts bitmap (quality: 50/100) to Base64 (String) */

        if (checkFileSize(bitmap, compress)){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compress ? COMPRESSION : 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            //return Base64.encodeToString(byteArray, Base64.DEFAULT);
            String source = Base64.encodeToString(byteArray, Base64.DEFAULT);
            try {
                return new String(source.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported encoding");
                e.printStackTrace();
                return null;
            }
        }
        System.out.println("Exceeded FILE_LIMIT_IN_KB");
        return null;
    }

    public String toUTF8(Drawable drawable, boolean compress){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return toUTF8(bitmap, compress);
    }

    public Bitmap toBitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap toBitmap(String string){
        /* Converts Base64 (String) to Bitmap */

        if (string != null && string.length() > 0){
            byte[] decodedString = Base64.decode(string, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return null;
    }

    public boolean saveToLocal(Context context, Bitmap bitmap, String filename){
        /* Save a bitmap to app-specific storage */

        try {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //No compression, will be saved locally
            byte[] bytes = byteArrayOutputStream.toByteArray();

            try (FileOutputStream fileOutputStream = context.openFileOutput( filename, Context.MODE_PRIVATE)) {
                fileOutputStream.write(bytes);
                fileOutputStream.flush();
            }

            return true; //successful
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; //failed
    }

    public void avatarToLocal(Context context, String avatar){
        try (FileOutputStream fileOutputStream = context.openFileOutput( "avatar.dat", Context.MODE_PRIVATE)) {
            fileOutputStream.write(avatar.getBytes());
            fileOutputStream.flush();
        }
        catch (Exception e){
            Log.d("LLS", e.getMessage());
            Toast.makeText(context, "Can't write user.dat. (LLS)", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveToLocal(Context context, String desc, String content){
        //Check if file exists
        File file = new File(context.getFilesDir() + "/user.dat");
        if (!file.exists()){
            try{
                FileOutputStream fileOuputStream = context.openFileOutput("user.dat", Context.MODE_PRIVATE);
            }
            catch (Exception e){
                Log.d("USER.DAT", "Error writing " + desc);
            }
        }
        //Create local storage copy of user data
        try (FileOutputStream fileOutputStream = context.openFileOutput( "user.dat", Context.MODE_APPEND)) {
            String data = desc + ":" + content + ";\n";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            Log.d("USER.DAT", content);
        }
        catch (Exception e){
            Log.d("LLS", e.getMessage());
            Toast.makeText(context, "Can't write user.dat. (LLS)", Toast.LENGTH_SHORT).show();
        }
    }

}
