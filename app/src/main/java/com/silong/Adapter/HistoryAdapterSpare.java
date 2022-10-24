package com.silong.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.Adoption;
import com.silong.dev.AdoptionHistory;
import com.silong.dev.HistoryData;
import com.silong.dev.R;

public class HistoryAdapterSpare extends RecyclerView.Adapter<HistoryAdapterSpare.ViewHolder> {

    HistoryData historyData[];
    Context context;

    public HistoryAdapterSpare(HistoryData[] historyData, AdoptionHistory activity){
        this.historyData = historyData;
        this.context = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.history_list_layout_spare, parent, false);
        HistoryAdapterSpare.ViewHolder viewHolder = new HistoryAdapterSpare.ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HistoryData historyDataList = historyData[position];
        holder.historyDate.setText("00/99/0909");
        holder.historyPetId.setText("Pet ID# ");
        holder.historyStatus.setText("Status");
    }

    @Override
    public int getItemCount() { return historyData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView historyDate;
        TextView historyPetId;
        TextView historyStatus;

        public ViewHolder (@NonNull View itemView){
            super(itemView);
            historyDate = itemView.findViewById(R.id.historyDate);
            historyPetId = itemView.findViewById(R.id.historyPetId);
            historyStatus = itemView.findViewById(R.id.historyStatus);
        }
    }
}
