package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Operation.Utility;
import com.silong.Task.AccountStatusChecker;
import com.silong.dev.AdoptionAgreement;
import com.silong.dev.Homepage;
import com.silong.dev.R;
import com.silong.dev.TermsOfUse;

public class ApplyDialog extends MaterialAlertDialogBuilder {

    public ApplyDialog(@NonNull Activity activity) {
        super((Context) activity);
        Context context = (Context) activity;
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setTitle("Adoption Process");
        super.setMessage("Begin adoption application for this pet?\n" +
                "Once an appointment is secured, please bring the following:\n" +
                "\t- 2x2 ID Picture\n\t- Cage or Leash (Kulungan o Tali)\n\t- Valid ID");

        super.setCancelable(false);

        LinearLayout applyDia_layout = new LinearLayout(context);
        applyDia_layout.setOrientation(LinearLayout.VERTICAL);
        applyDia_layout.setVerticalGravity(10);
        TextView conditionsTv = new TextView(context);
        conditionsTv.setPaintFlags(conditionsTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        conditionsTv.setText("Subject to Adoption Agreement");
        conditionsTv.setTextColor(context.getResources().getColor(R.color.purple_700));
        conditionsTv.setPadding(60,40,0,0);
        conditionsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.internetConnection(activity)){
                    Toast.makeText(activity, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(context, AdoptionAgreement.class);
                    context.startActivity(i);
                }

            }
        });
        applyDia_layout.addView(conditionsTv);

        super.setView(applyDia_layout);

        super.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountStatusChecker accountStatusChecker = new AccountStatusChecker(activity);
                accountStatusChecker.execute();

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
