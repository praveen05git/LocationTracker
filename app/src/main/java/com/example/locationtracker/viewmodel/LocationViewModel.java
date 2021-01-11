package com.example.locationtracker.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LocationViewModel extends AndroidViewModel {

    public MutableLiveData<String> locationData = new MutableLiveData<>();
    public MutableLiveData<Boolean> permission = new MutableLiveData<>();
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkLocation() {

        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationData.setValue(location.toString());
            }
        };
        permission.setValue(false);

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission.setValue(false);
        } else {
            permission.setValue(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 200, locationListener);
        }
    }


}
