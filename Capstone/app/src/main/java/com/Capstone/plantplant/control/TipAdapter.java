package com.capstone.plantplant.control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.R;
import com.capstone.plantplant.model.ItemTip;

import java.util.ArrayList;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.ViewHolder>{
    ArrayList<ItemTip> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_tip, parent, false);

        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ItemTip item = items.get(position);
        viewHolder.itemView.setLongClickable(true);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ItemTip item) {
        items.add(item);
    }

    public void setItems(ArrayList<ItemTip> items) {
        this.items = items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tip_title,tip_content;
        ImageView tip_image;

        public ViewHolder(View itemView) {
            super(itemView);

            tip_title = itemView.findViewById(R.id.tip_title);
            tip_image = itemView.findViewById(R.id.tip_image);
            tip_content = itemView.findViewById(R.id.tip_content);
        }
        public void setItem(final ItemTip item) {
            tip_title.setText(item.getTitle());
            tip_image.setImageResource(item.getRes());
            tip_image.setColorFilter(item.getColor());
            tip_content.setText(item.getContent());
        }
    }

}

