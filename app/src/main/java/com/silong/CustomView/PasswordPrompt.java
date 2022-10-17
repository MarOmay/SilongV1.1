package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.text.Html;

import android.text.method.PasswordTransformationMethod;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

import java.io.Serializable;
import java.util.Map;

public class PasswordPrompt extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Context context;

    private EditText pwET;
    private Map<String, Object> map;

    public PasswordPrompt(@NonNull Activity activity) {
        super((Context) activity);

        this.activity = activity;
        this.context = (Context) activity;

        super.setBackground(activity.getDrawable(R.drawable.dialog_bg));
        super.setTitle("Confirm Password");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setVerticalGravity(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(60,0,60,0);
        pwET = new EditText(context);
        pwET.setBackground(context.getResources().getDrawable(R.drawable.tf_background));
        pwET.setPadding(30,0,0,0);
        pwET.setHint("password");
        pwET.setTextSize(14);
        pwET.setLayoutParams(params);
        pwET.setTransformationMethod(new PasswordTransformationMethod());
        layout.addView(pwET);
        super.setView(layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(activity, "Processing", Toast.LENGTH_SHORT).show();

                //forward user input
                Intent intent = new Intent("password-submit");
                intent.putExtra("password", pwET.getText().toString());
                intent.putExtra("map", (Serializable) map);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

            }
        });

        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });

    }


    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
