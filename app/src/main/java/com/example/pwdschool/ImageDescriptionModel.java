package com.example.pwdschool;

public class ImageDescriptionModel {
    private String imageUrl;
    private String buildingName;
    private String date;
    private String description;

    public ImageDescriptionModel(String imageUrl, String buildingName, String date, String description) {
        this.imageUrl = imageUrl;
        this.buildingName = buildingName;
        this.date = date;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}

