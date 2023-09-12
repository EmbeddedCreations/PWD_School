package com.example.pwdschool;

import android.net.Uri;
import java.util.Date;
import java.util.List;

public class UploadData {
    private static UploadData instance;

    private Uri selectedImageUri;
    private String description;
    private Date dateTaken;
    private Date timeTaken;
    private double gpsLatitude;
    private double gpsLongitude;
    private List<String> selectedIssuesList;

    private UploadData() {
        // Private constructor to prevent instantiation.
    }

    public static UploadData getInstance() {
        if (instance == null) {
            instance = new UploadData();
        }
        return instance;
    }

    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    public void setSelectedImageUri(Uri selectedImageUri) {
        this.selectedImageUri = selectedImageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Date getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Date timeTaken) {
        this.timeTaken = timeTaken;
    }

    public double getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public double getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public List<String> getSelectedIssuesList() {
        return selectedIssuesList;
    }

    public void setSelectedIssuesList(List<String> selectedIssuesList) {
        this.selectedIssuesList = selectedIssuesList;
    }
}

