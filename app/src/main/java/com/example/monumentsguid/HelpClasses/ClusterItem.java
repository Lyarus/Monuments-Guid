package com.example.monumentsguid.HelpClasses;

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
    private String customImagePath;
    private String customImageDate;

    public ClusterItem(double lat, double lng, String name, String comment, String monument_image, String descrition, String image, String year, String id, int radius, boolean isHorizontal, String customImagePath, String customImageDate) {
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
        this.customImagePath = customImagePath;
        this.customImageDate = customImageDate;
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

    public String getDescription() {
        return description;
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

    public int getRadius() {
        return radius;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public String getCustomImagePath() {
        return customImagePath;
    }

    public String getCustomImageDate() {
        return customImageDate;
    }
}
