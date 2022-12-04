package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.dev.R;

public class ProcessDialog extends MaterialAlertDialogBuilder {

    ImageView processDialogPic;
    TextView processDialogId, processDialogType, processDialogColor, processDialogDateReq;

    public ProcessDialog(@NonNull Activity activity, Drawable drawable, String string) {

        super((Context) activity);
        super.setBackground(getContext().getDrawable(R.drawable.dialog_bg_2));

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.process_dialog_layout, null);
        processDialogPic = content.findViewById(R.id.processDialogPic);
        processDialogId = content.findViewById(R.id.processDialogId);
        processDialogType = content.findViewById(R.id.processDialogType);
        processDialogColor = content.findViewById(R.id.processDialogColor);
        processDialogDateReq = content.findViewById(R.id.processDialogDateReq);

        processDialogPic.setImageDrawable(drawable);
        processDialogType.setText(string);

        super.setView(content);

        super.setPositiveButton(Html.fromHtml("<b>" + "CLOSE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
