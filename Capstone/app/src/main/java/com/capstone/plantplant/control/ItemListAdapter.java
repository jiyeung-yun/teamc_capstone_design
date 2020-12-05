package com.capstone.plantplant.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.R;
import com.capstone.plantplant.model.ItemList;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> implements OnAdapterItemClickListener{
    ArrayList<ItemList> items = new ArrayList<>();
    OnAdapterItemClickListener onAdapterItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_list, parent, false);

        return new ViewHolder(itemView,onAdapterItemClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ItemList item = items.get(position);
        viewHolder.itemView.setLongClickable(true);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ItemList item) {
        items.add(item);
    }
    public ItemList getItem(int position) {
        return items.get(position);
    }
    public void setItems(ArrayList<ItemList> items) {
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
        TextView txt_listitem_name,txt_listitem_date;

        public ViewHolder(View itemView, final OnAdapterItemClickListener listener) {
            super(itemView);
            txt_listitem_name = itemView.findViewById(R.id.txt_listitem_name);
            txt_listitem_date = itemView.findViewById(R.id.txt_listitem_date);

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
        public void setItem(final ItemList item) {
            txt_listitem_name.setText(item.getName());
            txt_listitem_date.setText(item.getDate());
        }
    }

}

