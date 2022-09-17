package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class ApplyDialog extends MaterialAlertDialogBuilder {

    public ApplyDialog(@NonNull Context context) {
        super(context);
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage("Begin adoption application for this pet?\n" +
                "Once an appointment is secured, please bring the following:\n" +
                "\t- 2x2 ID Picture\n\t- Cage or Leash (Kulungan o Tali)\n\t- Valid ID");
        super.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //codes here
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
