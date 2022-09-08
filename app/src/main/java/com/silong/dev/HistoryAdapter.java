package com.silong.dev;

import android.content.Context;
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
        final HistoryData historyDataList = historyData[position];
        holder.petPic.setImageResource(historyDataList.getPetPic());
        holder.genderType.setText(historyDataList.getGenderType());
        holder.estAge.setText(historyDataList.getEstAge());
        holder.petColor.setText(historyDataList.getPetColor());
        holder.estSize.setText(historyDataList.getEstSize());
        holder.adoptDate.setText(historyDataList.getAdoptDate());
        holder.adoptStat.setText(historyDataList.getAdoptStat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, historyDataList.getGenderType(), Toast.LENGTH_SHORT).show();
            }
        });
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

