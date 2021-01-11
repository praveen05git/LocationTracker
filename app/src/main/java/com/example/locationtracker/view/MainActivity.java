package com.example.locationtracker.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.locationtracker.R;
import com.example.locationtracker.viewmodel.LocationViewModel;

public class MainActivity extends AppCompatActivity {

    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationViewModel.checkLocation();

        observeViewModel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationViewModel.checkLocation();
        }
    }

    public void observeViewModel() {

        locationViewModel.permission.observe(this, permissionGranted -> {
            if (!permissionGranted && permissionGranted != null) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        });

        locationViewModel.locationData.observe(this, hasData -> {
            if (hasData != null) {
                Toast.makeText(getApplication(), hasData, Toast.LENGTH_SHORT).show();
            }
        });

        locationViewModel.dataUploaded.observe(this, dataUploaded -> {
            if (dataUploaded) {
                Toast.makeText(getApplication(), "Data Uploaded", Toast.LENGTH_SHORT).show();
            }
        });

    }
}