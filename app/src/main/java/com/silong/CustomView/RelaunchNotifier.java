package com.silong.CustomView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.Splash;

public class RelaunchNotifier extends MaterialAlertDialogBuilder {

    public RelaunchNotifier(@NonNull Activity activity) {
        super(activity);

        super.setTitle("System Info");
        super.setMessage("Some information needs to be updated first.\nThis requires the app to be refreshed.");
        super.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(activity, Splash.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        super.setCancelable(false);
    }
}
