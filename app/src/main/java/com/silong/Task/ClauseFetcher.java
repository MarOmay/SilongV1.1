package com.silong.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silong.Object.AgreementData;
import com.silong.Operation.Utility;
import com.silong.dev.AdoptionAgreement;

public class ClauseFetcher extends AsyncTask {

    private Activity activity;

    public ClauseFetcher(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://silongdb-1-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mReference = mDatabase.getReference("publicInformation").child("adoptionAgreement");
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //read all entries
                    for (DataSnapshot snap : snapshot.getChildren()){

                        String [] clause = snap.getValue().toString().split(Utility.CLAUSE_SEPARATOR);

                        AgreementData agreementData = new AgreementData();

                        agreementData.setAgreementDate(snap.getKey());
                        agreementData.setAgreementTitle(clause[0]);
                        agreementData.setAgreementBody(clause[1]);

                        AdoptionAgreement.AGREEMENT_DATA.add(agreementData);

                    }

                    //refresh list
                    sendBroadcast();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utility.log("CF.dIB.oC: " + error.getMessage());
                }
            });
        }
        catch (Exception e){
            Utility.log("CF.dIB: " + e.getMessage());
        }

        return null;
    }

    private void sendBroadcast(){
        Intent intent = new Intent("refresh-agreement");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }
}
