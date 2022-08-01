package com.silong.CustomDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.Homepage;
import com.silong.dev.R;
import com.silong.dev.Splash;
import com.silong.dev.UserData;

public class ExitDialog extends MaterialAlertDialogBuilder {

    public ExitDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Exit"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.circlelogo_gradient));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("Are you sure you want to exit the app?\nThis will log you out of your account.\n");
        super.setPositiveButton(Html.fromHtml("<b>"+"LOGOUT"+"</b>"), new DialogInterface.OnClickListener() {
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
