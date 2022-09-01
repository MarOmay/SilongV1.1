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
        super.setTitle(Html.fromHtml("<b>"+"Account Deactivation"+"</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getResources().getString(R.string.requestDeactivationMessage));

        super.setPositiveButton(Html.fromHtml("<b>"+"YES"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //send broadcast to trigger process
                Intent intent = new Intent("deactivate-account-now");
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
