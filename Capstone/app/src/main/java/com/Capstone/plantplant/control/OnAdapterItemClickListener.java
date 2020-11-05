package com.capstone.plantplant.control;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface OnAdapterItemClickListener {
    public void onItemClick(RecyclerView.ViewHolder holder, View view, int position);
}
