package com.silong.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.CustomView.ImageDialog;
import com.silong.CustomView.ProcessDialog;
import com.silong.dev.HistoryData;
import com.silong.dev.ProcessData;
import com.silong.dev.R;

public class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ViewHolder>{

    private Activity activity;
    ProcessData processData[];
    Context context;

    public ProcessAdapter(ProcessData[] processData, Activity activity){
        this.processData = processData;
        this.context = activity;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.process_list_layout, parent, false);
        ViewHolder viewHolder = new ProcessAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessAdapter.ViewHolder holder, int position) {

        final ProcessData processDataList = processData[position];
        holder.processImage.setImageResource(processDataList.getProcessImage());
        holder.processGenderType.setText(processDataList.getProcessGenderType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessDialog processDialog = new ProcessDialog(activity, holder.processImage.getDrawable(), processDataList.getProcessGenderType());
                processDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return processData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView processImage;
        TextView processGenderType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            processImage = itemView.findViewById(R.id.processImage);
            processGenderType = itemView.findViewById(R.id.processGenderType);
        }
    }
}
