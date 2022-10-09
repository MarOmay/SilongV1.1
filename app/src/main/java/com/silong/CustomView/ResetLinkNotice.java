package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class ResetLinkNotice extends MaterialAlertDialogBuilder{


    public ResetLinkNotice(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Account Recovery"+"</b>"));
        super.setIcon(R.drawable.accrecovery_icon);
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getResources().getString(R.string.accRecovMsg));

        super.setCancelable(false);

        super.setPositiveButton(Html.fromHtml("<b>"+"OK"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Codes here
                //No codes here, no further action needed
            }
        });
    }

}
