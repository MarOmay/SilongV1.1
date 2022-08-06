package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class AccountActivationDialog extends MaterialAlertDialogBuilder {

    public AccountActivationDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Account Activation"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("Please tell us why you want to activate this account again.\n");

        LinearLayout editTextLayout = new LinearLayout(context);
        editTextLayout.setOrientation(LinearLayout.VERTICAL);
        editTextLayout.setVerticalGravity(10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 4.0f);
        params.setMargins(60,0,60,0);
        params.height = 400;
        EditText reasonEt = new EditText(context);
        reasonEt.setBackground(context.getResources().getDrawable(R.drawable.tf_background));
        reasonEt.setPadding(30,30,30,30);
        reasonEt.setTextSize(14);
        reasonEt.setLayoutParams(params);
        reasonEt.setSingleLine(false);
        reasonEt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        reasonEt.setVerticalScrollBarEnabled(true);
        reasonEt.setHorizontallyScrolling(false);
        reasonEt.setHorizontalScrollBarEnabled(false);
        reasonEt.setGravity(Gravity.TOP);
        editTextLayout.addView(reasonEt);

        super.setView(editTextLayout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("request-activation");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
