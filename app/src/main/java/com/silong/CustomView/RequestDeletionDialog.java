package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class RequestDeletionDialog extends MaterialAlertDialogBuilder {

    public RequestDeletionDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>" + "Request Deletion" + "</b>"));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getResources().getString(R.string.requestDeletionMessage));

        super.setPositiveButton(Html.fromHtml("<b>"+"DELETE MY ACCOUNT"+"</b>"),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
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
