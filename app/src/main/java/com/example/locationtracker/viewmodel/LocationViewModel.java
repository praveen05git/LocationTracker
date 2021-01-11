package com.example.locationtracker.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.locationtracker.model.LocationDetail;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationViewModel extends AndroidViewModel {

    public MutableLiveData<String> locationData = new MutableLiveData<>();
    public MutableLiveData<Boolean> permission = new MutableLiveData<>();
    public MutableLiveData<Boolean> dataUploaded = new MutableLiveData<>();
    private LocationDetail locationDetail;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference databaseReference;

    public LocationViewModel(@NonNull Application application) {
        super(application);
    }

    public void checkLocation() {

        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationData.setValue(location.toString());
                dataUpload();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission.setValue(false);
        } else {
            permission.setValue(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 0, locationListener);
        }
    }

    public void dataUpload() {
        databaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("locationData");
        locationDetail = new LocationDetail(locationData.getValue(), System.nanoTime());
        databaseReference.child(String.valueOf(System.nanoTime())).setValue(locationDetail);
        dataUploaded.setValue(true);
    }

}
