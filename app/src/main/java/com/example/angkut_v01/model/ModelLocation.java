package com.example.angkut_v01.model;

public class ModelLocation {

    private String fullname;
    private double latitude;
    private double longitude;

    public ModelLocation() {

    }

    public ModelLocation(String fullname, double latitude, double longitude) {
        this.fullname = fullname;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
}
