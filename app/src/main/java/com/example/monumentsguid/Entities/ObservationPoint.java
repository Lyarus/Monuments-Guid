package com.example.monumentsguid.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ObservationPoint implements Parcelable {
    private double latitude;
    private double longitude;
    private String id;
    private String comment;
    private String image;
    private String year;
    private boolean isHorizontal;
    private String monumentRef;
    private String customImagePath;
    private String customImageDate;
    public static final Creator<ObservationPoint> CREATOR = new Creator<ObservationPoint>() {
        @Override
        public ObservationPoint createFromParcel(Parcel source) {
            return new ObservationPoint(source);
        }

        @Override
        public ObservationPoint[] newArray(int size) {
            return new ObservationPoint[size];
        }
    };

    public ObservationPoint(String id, String comment, String image, double latitude, double longitude, String year, boolean isHorizontal, String monumentRef, String customImagePath, String customImageDate) {
        this.id = id;
        this.comment = comment;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.year = year;
        this.isHorizontal = isHorizontal;
        this.monumentRef = monumentRef;
        this.customImagePath = customImagePath;
        this.customImageDate = customImageDate;
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

    protected ObservationPoint(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.id = in.readString();
        this.comment = in.readString();
        this.image = in.readString();
        this.year = in.readString();
        this.isHorizontal = in.readByte() != 0;
        this.monumentRef = in.readString();
        this.customImagePath = in.readString();
    }

    public String getCustomImagePath() {
        return customImagePath;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public void setCustomImagePath(String customImagePath) {
        this.customImagePath = customImagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.id);
        dest.writeString(this.comment);
        dest.writeString(this.image);
        dest.writeString(this.year);
        dest.writeByte(this.isHorizontal ? (byte) 1 : (byte) 0);
        dest.writeString(this.monumentRef);
        dest.writeString(this.customImagePath);
    }

    public String getCustomImageDate() {
        return customImageDate;
    }

    public void setCustomImageDate(String customImageDate) {
        this.customImageDate = customImageDate;
    }
}
