package com.example.monumentsguid.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ObservationPoint implements Parcelable {
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

    public static final Parcelable.Creator<ObservationPoint> CREATOR = new Parcelable.Creator<ObservationPoint>() {
        @Override
        public ObservationPoint createFromParcel(Parcel source) {
            return new ObservationPoint(source);
        }

        @Override
        public ObservationPoint[] newArray(int size) {
            return new ObservationPoint[size];
        }
    };

    protected ObservationPoint(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.id = in.readString();
        this.comment = in.readString();
        this.image = in.readString();
        this.year = in.readString();
        this.monumentRef = in.readString();
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
        dest.writeString(this.monumentRef);
    }
}
