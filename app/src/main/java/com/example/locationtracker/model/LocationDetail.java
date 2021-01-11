package com.example.locationtracker.model;

public class LocationDetail {
    public String location;
    public long updatedTime;

    public LocationDetail(String location, long updatedTime) {
        this.location = location;
        this.updatedTime = updatedTime;
    }

    public String getLocation() {
        return location;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }
}
