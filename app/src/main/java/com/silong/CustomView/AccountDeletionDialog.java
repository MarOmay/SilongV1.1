package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class AccountDeletionDialog extends MaterialAlertDialogBuilder {

    public AccountDeletionDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>" + "Account Deletion" + "</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getResources().getString(R.string.requestDeletionMessage));

        super.setPositiveButton(Html.fromHtml("<b>"+"DELETE MY ACCOUNT"+"</b>"),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //send broadcast to trigger process
                Intent intent = new Intent("delete-account-now");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        super.setNegativeButton(Html.fromHtml("<b>"+"CANCEL"+"</b>"),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
            }
        });
    }
}
