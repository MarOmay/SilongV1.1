package com.silong.CustomView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;
import com.silong.dev.TermsOfUse;

public class TnCDialog extends MaterialAlertDialogBuilder {

    public TnCDialog(@NonNull Context context) {
        super(context);
        super.setTitle(Html.fromHtml("<b>"+"Terms of Use"+"</b>"));
        super.setIcon(context.getDrawable(R.drawable.circlelogo_gradient));
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setMessage(context.getResources().getString(R.string.msg));

        LinearLayout tnc_layout = new LinearLayout(context);
        tnc_layout.setOrientation(LinearLayout.VERTICAL);
        tnc_layout.setVerticalGravity(10);
        TextView tnc_tv = new TextView(context);
        tnc_tv.setText("Terms of Use");
        tnc_tv.setTextColor(context.getResources().getColor(R.color.purple_700));
        tnc_tv.setPadding(60,0,0,0);
        tnc_layout.addView(tnc_tv);
        tnc_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, TermsOfUse.class);
                context.startActivity(i);
            }
        });

        super.setView(tnc_layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"SUBMIT"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("tnc-accepted");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
