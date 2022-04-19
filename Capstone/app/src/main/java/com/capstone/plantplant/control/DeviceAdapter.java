package com.capstone.plantplant.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.R;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> implements OnAdapterItemClickListener{
    ArrayList<Integer> items = new ArrayList<>();
    OnAdapterItemClickListener onAdapterItemClickListener;

    public DeviceAdapter(ArrayList<Integer> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_device, parent, false);

        return new ViewHolder(itemView,onAdapterItemClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        int item = items.get(position);
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
    public void addItem(int item) {
        items.add(item);
    }

    public void setItems(ArrayList<Integer> items) {
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
        TextView txt_device_string;

        public ViewHolder(View itemView, final OnAdapterItemClickListener listener) {
            super(itemView);
            txt_device_string = itemView.findViewById(R.id.txt_device_string);

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
        public void setItem(final int item) {
            txt_device_string.setText(Integer.toString(item));

        }
    }

}

