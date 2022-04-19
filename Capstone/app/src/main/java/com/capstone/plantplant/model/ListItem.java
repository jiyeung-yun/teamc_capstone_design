package com.capstone.plantplant.model;

public class ListItem {
    private int index;
    private String name;
    private String path;
    private String filename;
    private boolean problem = false;

    public boolean isProblem() {
        return problem;
    }

    public void setProblem(boolean problem) {
        this.problem = problem;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String date) {
        this.path = date;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
