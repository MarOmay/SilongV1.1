package com.silong.dev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FaqsAdapter extends RecyclerView.Adapter<FaqsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Faqs> faqsArrayList;

    public FaqsAdapter(Context context, ArrayList<Faqs> faqsArrayList){
        this.context = context;
        this.faqsArrayList = faqsArrayList;
    }

    @NonNull
    @Override
    public FaqsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.help_list_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqsAdapter.MyViewHolder holder, int position) {

        Faqs faqs = faqsArrayList.get(position);
        holder.headingTv.setText(faqs.heading);
        holder.bodyTv.setText(faqs.body);

        boolean isVisible = faqs.visibility;
        holder.expandedLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return faqsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView headingTv, bodyTv;
        ConstraintLayout expandedLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            headingTv = itemView.findViewById(R.id.helpHeadingTv);
            bodyTv = itemView.findViewById(R.id.helpBodyTv);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);

            headingTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Faqs faqs = faqsArrayList.get(getAdapterPosition());
                    faqs.setVisibility(!faqs.isVisibility());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}