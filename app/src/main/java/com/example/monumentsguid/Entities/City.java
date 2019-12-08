package com.example.monumentsguid.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    public String id;
    public String name;
    public String image;
    private String countryRef;

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

    public static final Parcelable.Creator<City> CREATOR = new Parcelable.Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    private City(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.image = in.readString();
        this.countryRef = in.readString();
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
        dest.writeString(this.countryRef);
    }
}
