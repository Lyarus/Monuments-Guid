package com.example.monumentsguid.Entities;

public class City {
    public String id;
    public String name;
    public String image;
    public String countryRef;

    public City() {
    }

    public City(String id, String name, String image, String countryRef) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.countryRef = countryRef;
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

    public String getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(String countryRef) {
        this.countryRef = countryRef;
    }

}
