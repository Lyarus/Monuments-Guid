package com.example.monumentsguid;

import com.google.android.gms.maps.model.LatLng;

public class ClusterItem implements com.google.maps.android.clustering.ClusterItem {

    private final LatLng location;
    private String name;
    private String comment;
    private String description;

    ClusterItem(double lat, double lng, String name, String comment, String description) {
        location = new LatLng(lat, lng);
        this.name = name;
        this.comment = comment;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
