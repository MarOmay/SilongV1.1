package com.silong.CustomView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.silong.Object.Pet;
import com.silong.dev.R;

import java.util.Map;

public class ProcessDialog extends MaterialAlertDialogBuilder {

    public ProcessDialog(@NonNull Activity activity, Map<String, Object> map) {

        super((Context) activity);
        super.setBackground(getContext().getDrawable(R.drawable.dialog_bg_2));

        ImageView processDialogPic;
        TextView processDialogId, processDialogType, processDialogColor, processDialogDateReq;

        LayoutInflater inflater = activity.getLayoutInflater();
        View content = inflater.inflate(R.layout.process_dialog_layout, null);
        processDialogPic = content.findViewById(R.id.processDialogPic);
        processDialogId = content.findViewById(R.id.processDialogId);
        processDialogType = content.findViewById(R.id.processDialogType);
        processDialogColor = content.findViewById(R.id.processDialogColor);
        processDialogDateReq = content.findViewById(R.id.processDialogDateReq);

        processDialogPic.setImageBitmap((Bitmap) map.get("pic"));
        processDialogId.setText(map.get("petID").toString());
        processDialogType.setText(map.get("genderType").toString());
        processDialogColor.setText(map.get("color").toString());
        processDialogDateReq.setText(map.get("age").toString());

        super.setView(content);

        super.setPositiveButton(Html.fromHtml("<b>" + "CLOSE" + "</b>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //code here
            }
        });
    }
}
