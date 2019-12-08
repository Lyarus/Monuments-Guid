package com.example.monumentsguid.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Monument implements Parcelable {
    public String id;
    public String name;
    public String image;
    private String description;
    private double latitude;
    private double longitude;
    private String cityRef;

    public Monument() {

    }

    public Monument(String id, String name, String image, double latitude, double longitude, String cityRef, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cityRef = cityRef;
        this.description = description;
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

    private Monument(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.cityRef = in.readString();
    }

    public String getDescription() {
        return description;
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

    public static final Parcelable.Creator<Monument> CREATOR = new Parcelable.Creator<Monument>() {
        @Override
        public Monument createFromParcel(Parcel source) {
            return new Monument(source);
        }

        @Override
        public Monument[] newArray(int size) {
            return new Monument[size];
        }
    };

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.description);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.cityRef);
    }
}
