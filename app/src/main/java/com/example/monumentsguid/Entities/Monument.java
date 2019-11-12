package com.example.monumentsguid.Entities;

public class Monument {
    public String id;
    public String name;
    public String image;
    public double latitude;
    public double longitude;
    public String cityRef;

    public Monument() {

    }

    public Monument(String id, String name, String image, double latitude, double longitude, String cityRef) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityRef = cityRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCityRef() {
        return cityRef;
    }

    public void setCityRef(String cityRef) {
        this.cityRef = cityRef;
    }
}
