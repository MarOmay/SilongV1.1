package com.silong.dev;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    HistoryData historyData[];
    Context context;

    public HistoryAdapter(HistoryData[] historyData, AdoptionHistory activity){
        this.historyData = historyData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.history_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final HistoryData historyDataList = historyData[position];
            holder.petPic.setImageBitmap(historyDataList.getPetPic());
            holder.genderType.setText(historyDataList.getGenderType());
            holder.estAge.setText(historyDataList.getEstAge());
            holder.petColor.setText(historyDataList.getPetColor());
            holder.estSize.setText(historyDataList.getEstSize());
            holder.adoptDate.setText(historyDataList.getAdoptDate());

            String status = "";
            switch (historyDataList.getAdoptStat()){
                case Timeline.SEND_REQUEST:
                case Timeline.AWAITING_APPROVAL:
                case Timeline.REQUEST_APPROVED:
                case Timeline.SET_APPOINTMENT:
                case Timeline.APPOINTMENT_CONFIRMED:
                    holder.adoptStat.setTextColor(Color.YELLOW);
                    status = "PENDING";
                    break;

                case Timeline.ADOPTION_SUCCESSFUL:
                case Timeline.FINISHED:
                    holder.adoptStat.setTextColor(Color.GREEN);
                    status = "ADOPTION SUCCESSFUL";
                    break;

                case Timeline.CANCELLED:
                    holder.adoptStat.setTextColor(Color.RED);
                    status = "CANCELLED";
                    break;
                case Timeline.DECLINED:
                    holder.adoptStat.setTextColor(Color.RED);
                    status = "DECLINED";
                    break;
            }

            holder.adoptStat.setText(status);
        }
        catch (Exception e){
            holder.itemView.setVisibility(View.GONE);
            Log.d("HistoryAdapter", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return historyData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView petPic;
        TextView genderType;
        TextView estAge;
        TextView petColor;
        TextView estSize;
        TextView adoptDate;
        TextView adoptStat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            petPic = itemView.findViewById(R.id.historyPetPicIv);
            genderType = itemView.findViewById(R.id.historyGenderTypeTv);
            estAge = itemView.findViewById(R.id.historyAgeTv);
            petColor = itemView.findViewById(R.id.historyColorTv);
            estSize = itemView.findViewById(R.id.historySizeTv);
            adoptDate = itemView.findViewById(R.id.historyAdoptDateTv);
            adoptStat = itemView.findViewById(R.id.historyAdoptStatTv);
        }
    }
}

