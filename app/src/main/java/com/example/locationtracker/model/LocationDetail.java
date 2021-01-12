package com.example.locationtracker.model;

public class LocationDetail {
    public String location;
    public String updatedTime;

    public LocationDetail() {
    }

    public LocationDetail(String location, String updatedTime) {
        this.location = location;
        this.updatedTime = updatedTime;
    }

    public String getLocation() {
        return location;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }
}
