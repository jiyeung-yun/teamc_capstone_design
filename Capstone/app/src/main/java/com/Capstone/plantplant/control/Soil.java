package com.capstone.plantplant.control;

public class Soil {
    private int SID;
    private String Sname;
    private String Stype;
    private String Sproduce;
    private String Susage;
    private String Scharacter;

    public String getScharacter() {
        return Scharacter;
    }

    public void setScharacter(String scharacter) {
        Scharacter = scharacter;
    }

    public int getSID() {
        return SID;
    }

    public void setSID(int SID) {
        this.SID = SID;
    }

    public String getSname() {
        return Sname;
    }

    public void setSname(String sname) {
        Sname = sname;
    }

    public String getStype() {
        return Stype;
    }

    public void setStype(String stype) {
        Stype = stype;
    }

    public String getSproduce() {
        return Sproduce;
    }

    public void setSproduce(String sproduce) {
        Sproduce = sproduce;
    }

    public String getSusage() {
        return Susage;
    }

    public void setSusage(String susage) {
        Susage = susage;
    }
}
