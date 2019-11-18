package com.example.monumentsguid;

import com.google.android.gms.maps.model.LatLng;

public class ClusterItem implements com.google.maps.android.clustering.ClusterItem {

    private final LatLng location;
    private String name;
    private String comment;
    private String monument_image;
    private String description;

    ClusterItem(double lat, double lng, String name, String comment, String monument_image, String descrition) {
        location = new LatLng(lat, lng);
        this.name = name;
        this.comment = comment;
        this.monument_image = monument_image;
        this.description = descrition;
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
}
