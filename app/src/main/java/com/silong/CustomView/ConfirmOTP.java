package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Task.AccountStatusChecker;
import com.silong.dev.R;

public class ConfirmOTP extends MaterialAlertDialogBuilder {

    private Activity activity;
    private Context context;
    private String email;

    private EditText otpET;

    public ConfirmOTP(@NonNull Activity activity){//, String email) {
        super((Context) activity);

        this.activity = activity;
        this.context = (Context) activity;

        //this.email = email;

        super.setBackground(activity.getDrawable(R.drawable.dialog_bg));
        super.setTitle("Confirm One-time PIN");
        super.setMessage("Enter OTP sent to " + email);

        super.setCancelable(false);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setVerticalGravity(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(60,0,60,0);
        otpET = new EditText(context);
        otpET.setBackground(context.getResources().getDrawable(R.drawable.tf_background));
        otpET.setPadding(30,0,0,0);
        otpET.setHint("XXXXXX");
        otpET.setTextSize(14);
        otpET.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        otpET.setLetterSpacing(00.50F);
        otpET.setTypeface(Typeface.DEFAULT_BOLD);
        otpET.setLayoutParams(params);
        otpET.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(otpET);
        super.setView(layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //forward user input
                Intent intent = new Intent("otp-submit");
                intent.putExtra("otp", otpET.getText().toString());
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

}
