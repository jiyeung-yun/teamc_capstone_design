package com.capstone.plantplant.model;

import android.content.Context;

import com.capstone.plantplant.util.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlantToServer {
    private String plant_name;
    private int standard_humidity;
    private String water_date;
    private float humidity_sensor = 0 ;
    private float temperature_sensor = 0;
    private int waterlevel_sensor = 0;
    private int device_id;
    private String userid;
    private boolean watering=false;

    public boolean isWatering() {
        return watering;
    }

    public void setWatering(boolean watering) {
        this.watering = watering;
    }

    public int isWaterlevel_sensor() {
        return waterlevel_sensor;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public PlantToServer(String plant_name,Context context) {
        this.plant_name = plant_name;
        this.userid = new PreferenceManager().getString(context,"USER_ID");
    }
    public PlantToServer(String plant_name, int standard_humidity, int device_id,Context context) {
        this.userid = new PreferenceManager().getString(context,"USER_ID");
        this.plant_name = plant_name;
        this.standard_humidity = standard_humidity;
        this.device_id = device_id;
        this.water_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    public PlantToServer(String plant_name, int standard_humidity,Context context) {
        this.userid = new PreferenceManager().getString(context,"USER_ID");
        this.plant_name = plant_name;
        this.standard_humidity = standard_humidity;
    }

    public String getWater_date() {
        return water_date;
    }

    public void setWater_date(String water_date) {
        this.water_date = water_date;
    }

    public int getDevice_id() {
        return device_id;
    }

    public void setDevice_id(int device_id) {
        this.device_id = device_id;
    }

    public String getPlant_name() {
        return plant_name;
    }

    public void setPlant_name(String plant_name) {
        this.plant_name = plant_name;
    }

    public float getStandard_humidity() {
        return standard_humidity;
    }

    public void setStandard_humidity(int standard_humidity) {
        this.standard_humidity = standard_humidity;
    }

    public float getHumidity_sensor() {
        return humidity_sensor;
    }

    public void setHumidity_sensor(float humidity_sensor) {
        this.humidity_sensor = humidity_sensor;
    }

    public float getTemperature_sensor() {
        return temperature_sensor;
    }

    public void setTemperature_sensor(float temperature_sensor) {
        this.temperature_sensor = temperature_sensor;
    }

    public int getWaterlevel_sensor() {
        return waterlevel_sensor;
    }

    public void setWaterlevel_sensor(int waterlevel_sensor) {
        this.waterlevel_sensor = waterlevel_sensor;
    }
}
