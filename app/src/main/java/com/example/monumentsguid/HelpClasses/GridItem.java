package com.example.monumentsguid.HelpClasses;

public class GridItem {
    private String name;
    private String image;
    private String id;
    private boolean isActive;

    public GridItem(String id, String name, String image, boolean isActive) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.isActive = isActive;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    boolean isActive() {
        return isActive;
    }
}
