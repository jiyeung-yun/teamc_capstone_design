package com.capstone.plantplant.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.R;
import com.capstone.plantplant.model.Plant;

import java.util.ArrayList;

public class KindSearchAdapter extends RecyclerView.Adapter<KindSearchAdapter.ViewHolder> implements OnAdapterItemClickListener{
    ArrayList<Plant> items = new ArrayList<>();
    OnAdapterItemClickListener onAdapterItemClickListener;

    public KindSearchAdapter(ArrayList<Plant> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_search, parent, false);

        return new ViewHolder(itemView,onAdapterItemClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Plant item = items.get(position);
        viewHolder.itemView.setLongClickable(true);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void clear(){
        items.clear();
    }
    public void addItem(Plant item) {
        items.add(item);
    }

    public void setItems(ArrayList<Plant> items) {
        this.items = items;
    }
    public void setOnItemClickListener(OnAdapterItemClickListener listener) {
        this.onAdapterItemClickListener = listener;
    }
    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
        if(onAdapterItemClickListener != null){
            onAdapterItemClickListener.onItemClick(holder,view,position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_kind_string,txt_kind_string2,txt_kind_string3;

        public ViewHolder(View itemView, final OnAdapterItemClickListener listener) {
            super(itemView);
            txt_kind_string = itemView.findViewById(R.id.txt_kind_string);
            txt_kind_string2 = itemView.findViewById(R.id.txt_kind_string2);
            txt_kind_string3 = itemView.findViewById(R.id.txt_kind_string3);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, v, postion);
                    }
                }
            });
        }
        public void setItem(final Plant item) {
            txt_kind_string.setText(item.getPname());
            if(item.getPexp()==null){
                txt_kind_string2.setVisibility(View.GONE);
                txt_kind_string3.setVisibility(View.GONE);
            }else {
                txt_kind_string2.setVisibility(View.VISIBLE);
                txt_kind_string3.setVisibility(View.VISIBLE);
                txt_kind_string3.setText(item.getPexp());
            }


        }
    }

}

