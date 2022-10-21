package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class PendingApplicationNotice extends MaterialAlertDialogBuilder {
    public PendingApplicationNotice(@NonNull Context context) {
        super(context);
        super.setTitle("Notice");
        super.setMessage("You have a PENDING application.\nCheck your adoption history.\nPlease refresh the app.");
        super.setBackground(context.getResources().getDrawable(R.drawable.dialog_bg));

        super.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }
}
