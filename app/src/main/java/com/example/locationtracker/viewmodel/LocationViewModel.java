package com.example.locationtracker.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.locationtracker.model.LocationDetail;
import com.example.locationtracker.model.RecentData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationViewModel extends AndroidViewModel {

    public MutableLiveData<String> locationData = new MutableLiveData<>();
    public MutableLiveData<Boolean> permission = new MutableLiveData<>();
    public MutableLiveData<Boolean> dataUploaded = new MutableLiveData<>();
    public MutableLiveData<String> uploadCounts = new MutableLiveData<>();
    public MutableLiveData<String> recentUpload = new MutableLiveData<>();

    private List<LocationDetail> newLocationList = new ArrayList<>();

    private LocationDetail locationDetail;
    private RecentData recentData;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference databaseReference;
    private DatabaseReference recentDataReference;

    private String dateTime = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a", Locale.getDefault()).format(new Date());
    private String today = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

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
                Toast.makeText(getApplication(), "Turn on location", Toast.LENGTH_LONG).show();
            }
        };

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission.setValue(false);
        } else {
            permission.setValue(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        }
    }

    public void dataUpload() {
        databaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("locationData");
        recentDataReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("recentData");

        dateTime = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a", Locale.getDefault()).format(new Date());
        locationDetail = new LocationDetail(locationData.getValue(), dateTime);
        recentData = new RecentData(dateTime);

        databaseReference.child(today).child(String.valueOf(System.nanoTime())).setValue(locationDetail);
        recentDataReference.child(today).setValue(dateTime);

        dataUploaded.setValue(true);
    }

    public void getData() {
        newLocationList.clear();
        databaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("locationData/" + today + "/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newLocationList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    locationDetail = ds.getValue(LocationDetail.class);
                    newLocationList.add(locationDetail);
                    uploadCounts.setValue(String.valueOf(newLocationList.size()));
                    getRecentTime();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getRecentTime() {
        recentDataReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("recentData/" + today + "/");
        recentDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    recentUpload.setValue(dataSnapshot.getValue().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
