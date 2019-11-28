package com.example.monumentsguid.Entities;

import java.util.Date;

public class Image {
    private String path;
    private Date date;

    public Image(String path, Date date) {
        this.path = path;
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
