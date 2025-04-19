package com.example.makerthon;

import android.widget.ImageView;

public class ReportItem {
    private String id;
    private String userName;
    private String reportType;
    private String location;
    private String description;
    private ImageView image1, image2, image3;
    public ReportItem(String type, String location, String description, ImageView imageUri1, ImageView imageUri2, ImageView imageUri3) {
        this.reportType = type;
        this.location = location;
        this.description = description;
        this.image1 = imageUri1;
        this.image2 = imageUri2;
        this.image3 = imageUri3;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageView getImageUri1() {
        return image1;
    }

    public void setImageUri1(ImageView image1) {
        this.image1 = image1;
    }

    public ImageView getImageUri2() {
        return image2;
    }

    public void setImageUri2(ImageView image2) {
        this.image2 = image2;
    }

    public ImageView getImageUri3() {
        return image3;
    }

    public void setImageUri3(ImageView image3) {
        this.image3 = image3;
    }

    public String getType() {
        return reportType;
    }
}
