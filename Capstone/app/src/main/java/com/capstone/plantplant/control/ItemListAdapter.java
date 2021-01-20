package com.capstone.plantplant.control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.plantplant.R;
import com.capstone.plantplant.model.ListItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> implements OnAdapterItemClickListener{
    ArrayList<ListItem> items = new ArrayList<>();
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
        ListItem item = items.get(position);
        viewHolder.itemView.setLongClickable(true);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ListItem item) {
        items.add(item);
    }
    public ListItem getItem(int position) {
        return items.get(position);
    }
    public void setItems(ArrayList<ListItem> items) {
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
        TextView txt_listitem_name;
        ImageView img_listitem;

        public ViewHolder(View itemView, final OnAdapterItemClickListener listener) {
            super(itemView);
            txt_listitem_name = itemView.findViewById(R.id.txt_listitem_name);

            img_listitem = itemView.findViewById(R.id.img_listitem);
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
        public void setItem(final ListItem item) {
            txt_listitem_name.setText(item.getName());

            String path = item.getPath();
            String filename = item.getFilename();

            if(filename!=null && path!=null){
                try {
                    File file=new File(path, filename);
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    img_listitem.setImageBitmap(bitmap);
                    img_listitem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

