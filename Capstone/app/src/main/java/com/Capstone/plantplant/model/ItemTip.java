package com.capstone.plantplant.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ItemTip {
    private String title;
    private int res;
    private String content;
    private int color;
    String page;

    public ItemTip(String t,int r,String c,int co,String p){
        this.title = t;
        this.res = r;
        this.content = c;
        this.color = co;
        this.page = p;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }
}
