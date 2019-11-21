package com.example.monumentsguid;

import com.google.android.gms.maps.model.LatLng;

public class ClusterItem implements com.google.maps.android.clustering.ClusterItem {

    private final LatLng location;
    private String name;
    private String comment;
    private String monument_image;
    private String description;
    private String image;
    private String year;
    private String id;
    private int radius;
    private boolean isHorizontal;

    ClusterItem(double lat, double lng, String name, String comment, String monument_image, String descrition, String image, String year, String id, int radius, boolean isHorizontal) {
        this.image = image;
        this.year = year;
        this.id = id;
        this.radius = radius;
        this.isHorizontal = isHorizontal;
        location = new LatLng(lat, lng);
        this.name = name;
        this.comment = comment;
        this.monument_image = monument_image;
        this.description = descrition;
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

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return comment;
    }

    public String getMonument_image() {
        return monument_image;
    }

    public void setMonument_image(String monument_image) {
        this.monument_image = monument_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }
}
