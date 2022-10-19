package com.silong.dev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.silong.Object.Faqs;

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

        if (Help.KEYWORD.length() == 0 ||
                faqs.getHeading().toLowerCase().contains(Help.KEYWORD) ||
                faqs.getBody().toLowerCase().contains(Help.KEYWORD) ||
                faqs.isInTags(Help.KEYWORD)){

            holder.headingTv.setText(faqs.getHeading());
            holder.bodyTv.setText(faqs.getBody());

            holder.expandedLayout.setVisibility(faqs.isVisibility() ? View.VISIBLE : View.GONE);
        }
        else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

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