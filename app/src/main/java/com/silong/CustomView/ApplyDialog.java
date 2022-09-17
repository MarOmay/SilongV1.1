package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Task.AccountStatusChecker;
import com.silong.dev.Homepage;
import com.silong.dev.R;

public class ApplyDialog extends MaterialAlertDialogBuilder {

    public ApplyDialog(@NonNull Activity activity) {
        super((Context) activity);
        Context context = (Context) activity;
        super.setBackground(context.getDrawable(R.drawable.dialog_bg));
        super.setTitle("Adoption Process");
        super.setMessage("Begin adoption application for this pet?\n" +
                "Once an appointment is secured, please bring the following:\n" +
                "\t- 2x2 ID Picture\n\t- Cage or Leash (Kulungan o Tali)\n\t- Valid ID");
        super.setPositiveButton(Html.fromHtml("<b>"+"APPLY"+"</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountStatusChecker accountStatusChecker = new AccountStatusChecker(activity);
                accountStatusChecker.execute();
                try{
                    LoadingDialog loadingDialog = new LoadingDialog(activity);
                    loadingDialog.startLoadingDialog();
                }
                catch (Exception e){
                    Log.d("DEBUGGER>>>", "Failed to start LoadingDialog at ApplyDialog.");
                }

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
