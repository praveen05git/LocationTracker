package com.example.locationtracker.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

    public MutableLiveData<String> locationLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> permissionLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> dataUploadedLiveData = new MutableLiveData<>();
    public MutableLiveData<String> uploadCountsLiveData = new MutableLiveData<>();
    public MutableLiveData<String> recentUploadLiveData = new MutableLiveData<>();

    private List<LocationDetail> newLocationList = new ArrayList<>();

    private LocationDetail locationDetail;
    private RecentData recentData;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference locationDatabaseReference;
    private DatabaseReference recentDatabaseReference;

    private String dateTime = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a", Locale.getDefault()).format(new Date());
    private String today = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

    public LocationViewModel(@NonNull Application application) {
        super(application);
    }

    //Requesting Location from device
    public void checkLocation() {
        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationLiveData.setValue(location.toString());
                dataUpload();
            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplication(), "Turn on location", Toast.LENGTH_LONG).show();
            }
        };

        if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLiveData.setValue(false);
        } else {
            permissionLiveData.setValue(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        }
    }

    //Uploading Location and Recent Data to Firebase
    public void dataUpload() {
        locationDatabaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("locationData");
        recentDatabaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("recentData");

        dateTime = new SimpleDateFormat("dd/MM/yyy hh:mm:ss a", Locale.getDefault()).format(new Date());
        locationDetail = new LocationDetail(locationLiveData.getValue(), dateTime);
        recentData = new RecentData(dateTime);

        locationDatabaseReference.child(today).child(String.valueOf(System.nanoTime())).setValue(locationDetail);
        recentDatabaseReference.child(today).setValue(dateTime);

        dataUploadedLiveData.setValue(true);
    }

    //Fetching Location count from Firebase
    public void getData() {
        newLocationList.clear();

        locationDatabaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("locationData/" + today + "/");
        locationDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newLocationList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    locationDetail = ds.getValue(LocationDetail.class);
                    newLocationList.add(locationDetail);
                    uploadCountsLiveData.setValue(String.valueOf(newLocationList.size()));

                    getRecentActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Fetching Recent Activity from Firebase
    public void getRecentActivity() {
        recentDatabaseReference = FirebaseDatabase.getInstance("https://locationtracker-8c20b-default-rtdb.firebaseio.com/").getReference("recentData/" + today + "/");
        recentDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    recentUploadLiveData.setValue(dataSnapshot.getValue().toString());
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
