package com.silong.dev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.silong.Object.AgreementData;
import com.silong.Operation.Utility;
import com.silong.Task.ClauseFetcher;

import java.util.ArrayList;
import java.util.Comparator;

public class AdoptionAgreement extends AppCompatActivity {

    LinearLayout conditionsLayout;
    private TextView bodyTv;

    public static ArrayList<AgreementData> AGREEMENT_DATA = new ArrayList<>();

    private LoadingDialog loadingDialog = new LoadingDialog(AdoptionAgreement.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_agreement);
        getSupportActionBar().hide();

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mReloadReceiver, new IntentFilter("refresh-agreement"));

        conditionsLayout = findViewById(R.id.conditionsLayout);
        bodyTv = findViewById(R.id.bodyTv);

        /*
        TextView conditionsTitle = new TextView(this);
        conditionsTitle.setText(R.string.adoptionConditions1);
        conditionsTitle.setTextSize(18);
        conditionsTitle.setTextColor(getResources().getColor(R.color.black));

        TextView conditionsBody = new TextView(this);
        conditionsBody.setText(R.string.adoptionConditions2);
        conditionsBody.setTextSize(16);
        conditionsBody.setTextColor(getResources().getColor(R.color.black));

        conditionsLayout.addView(conditionsTitle);
        conditionsLayout.addView(conditionsBody);*/

        AGREEMENT_DATA.clear();

        ClauseFetcher clauseFetcher = new ClauseFetcher(AdoptionAgreement.this);
        clauseFetcher.execute();

        loadingDialog.startLoadingDialog();

    }

    private void loadContents(){
        if (AGREEMENT_DATA.isEmpty()){
            Toast.makeText(this, "No agreement written yet", Toast.LENGTH_SHORT).show();
            Utility.log("AdpAgmnt.lC: No logs to be displayed");
            return;
        }

        try {

            String contents = "";

            //sort by date
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AGREEMENT_DATA.sort(new Comparator<AgreementData>() {
                    @Override
                    public int compare(AgreementData a1, AgreementData a2) {
                        return a1.getAgreementDate().compareTo(a2.getAgreementDate());
                    }
                });
            }

            for (AgreementData agData : AGREEMENT_DATA){
                contents += agData.getAgreementTitle() + "\n";
                contents += agData.getAgreementBody() + "\n\n";
            }

            String lastModified = AGREEMENT_DATA.get(AGREEMENT_DATA.size()-1).getAgreementDate().split("--")[0];

            contents += "Last Modified: " + lastModified;

            bodyTv.setText(contents);

        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            Utility.log("AdpAgmnt.lC: " + e.getMessage());
        }

        loadingDialog.dismissLoadingDialog();
    }


    private BroadcastReceiver mReloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            loadContents();

        }
    };

    public void back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReloadReceiver);
        super.onDestroy();
    }
}