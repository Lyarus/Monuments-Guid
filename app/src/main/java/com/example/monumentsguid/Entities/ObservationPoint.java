package com.example.monumentsguid.Entities;

public class ObservationPoint {
    public double latitude;
    public double longitude;
    String id;
    String comment;
    String image;
    String year;
    String monumentRef;

    public ObservationPoint() {

    }

    public ObservationPoint(String id, String comment, String image, double latitude, double longitude, String year, String monumentRef) {
        this.id = id;
        this.comment = comment;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.year = year;
        this.monumentRef = monumentRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonumentRef() {
        return monumentRef;
    }

    public void setMonumentRef(String monumentRef) {
        this.monumentRef = monumentRef;
    }
}
