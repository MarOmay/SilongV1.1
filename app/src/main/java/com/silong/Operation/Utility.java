package com.silong.Operation;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static boolean internetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null){
            return true;
        }
        return false;
    }

    public void passwordFieldTransformer(EditText field, boolean visible){
        field.setTransformationMethod(visible ? null : new PasswordTransformationMethod());
    }

    public static String dateToday(){
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

}
