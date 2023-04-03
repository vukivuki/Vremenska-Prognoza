package com.example.weatherapp;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "City")
public class City {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String Country;
    private String CityName;
    private String desc;
    private int temperature;

    public City(String Country, String CityName, String desc, int temperature) {
        this.Country = Country;
        this.CityName = CityName;
        this.desc = desc;
        this.temperature = temperature;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return Country;
    }

    public String getCityName() {
        return CityName;
    }

    public String getDesc() {
        return desc;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.Country = country;
    }

    public void setCityName(String cityName) {
        this.CityName = cityName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "City{" +
                ", Country='" + Country + '\'' +
                ", CityName='" + CityName + '\'' +
                ", temperature=" + temperature +
                ", desc='" + desc + '\'' +
                '}';
    }
}
