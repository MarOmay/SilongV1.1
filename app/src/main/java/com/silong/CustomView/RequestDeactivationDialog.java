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

public class RequestDeactivationDialog extends MaterialAlertDialogBuilder {

    public RequestDeactivationDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Request Deactivation"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getResources().getString(R.string.requestDeactivationMessage));

        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // codes here
                RequestDeactivationDialog.super.setTitle(Html.fromHtml("<b>"+"Request Deactivation"+"</b>"));
                RequestDeactivationDialog.super.setBackground(context.getDrawable(R.drawable.dialog_bg));
                RequestDeactivationDialog.super.setMessage("We'll be glad to know your reason for deactivating.");

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

                RequestDeactivationDialog.super.setView(editTextLayout);

                RequestDeactivationDialog.super.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //send broadcast to trigger process
                        Intent intent = new Intent("deactivate-account-now");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
                RequestDeactivationDialog.super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //codes here
                    }
                });
                RequestDeactivationDialog.super.show();
            }
        });
        super.setNegativeButton(Html.fromHtml("<b>"+"NO"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
    }
}
