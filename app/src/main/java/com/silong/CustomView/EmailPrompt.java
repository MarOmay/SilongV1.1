package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.InputValidator;
import com.silong.dev.R;

public class EmailPrompt  extends MaterialAlertDialogBuilder{

    public EmailPrompt(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Forgot Password"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.forgotpass_icon));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("\nEnter registered email address.");

        LinearLayout recov_layout = new LinearLayout(context);
        recov_layout.setOrientation(LinearLayout.VERTICAL);
        recov_layout.setVerticalGravity(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(60,0,60,0);
        EditText et_recovEmail = new EditText(context);
        et_recovEmail.setBackground(context.getResources().getDrawable(R.drawable.tf_background));
        et_recovEmail.setPadding(30,0,0,0);
        et_recovEmail.setHint("Email Address");
        et_recovEmail.setTextSize(14);
        et_recovEmail.setLayoutParams(params);
        et_recovEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        recov_layout.addView(et_recovEmail);
        super.setView(recov_layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Check if field is empty
                String email = et_recovEmail.getText().toString().trim();
                if(email.equals("")){
                    Toast.makeText(context, "Please enter your email.", Toast.LENGTH_SHORT).show();
                }
                else if (!InputValidator.checkEmail(email)){
                    Toast.makeText(context, "Please check the format of your email.", Toast.LENGTH_SHORT).show();
                }else{
                    //send email to next activity
                    Intent intent = new Intent("reset-password-email");
                    intent.putExtra("email", email);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                }
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
