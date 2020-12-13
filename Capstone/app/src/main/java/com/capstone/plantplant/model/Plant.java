package com.capstone.plantplant.model;

public class Plant {
    private int PID;
    private String Pname;
    private String Pexp;
    private int Pwater;
    private String Ptime;

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getPexp() {
        return Pexp;
    }

    public void setPexp(String pexp) {
        Pexp = pexp;
    }

    public int getPwater() {
        return Pwater;
    }

    public void setPwater(int pwater) {
        Pwater = pwater;
    }

    public String getPtime() {
        return Ptime;
    }

    public void setPtime(String ptime) {
        Ptime = ptime;
    }
}
