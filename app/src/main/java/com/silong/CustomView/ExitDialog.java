package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class ExitDialog extends MaterialAlertDialogBuilder {

    public ExitDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Logout"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.circlelogo_gradient));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("Are you sure you want to logout from your account?\n");
        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("logout-user");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
