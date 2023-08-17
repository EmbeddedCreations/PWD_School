package com.example.pwdschool;

public class ImageDescriptionModel {
    private final String imageUrl;
    private final String buildingName;
    private final String date;
    private final String description;

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

