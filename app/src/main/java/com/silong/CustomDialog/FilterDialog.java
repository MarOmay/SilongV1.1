package com.silong.CustomDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class FilterDialog extends MaterialAlertDialogBuilder {
    public FilterDialog(@NonNull Context context) {
        super(context);
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setView(R.layout.filter_layout);
        super.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Codes here
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
