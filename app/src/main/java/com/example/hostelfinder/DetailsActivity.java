package com.example.hostelfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.databinding.ActivityDetailsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int GPS_REQUEST_CODE = 1002;

    private String currentLocation = "";
    private String hostelLocation = "";
    private boolean userClickedLocation = false; // to remember if user clicked before turning GPS on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get data from Intent
        String hostel = getIntent().getStringExtra("Name_Of_Hostel");
        String img = getIntent().getStringExtra("img");
        String Contact = getIntent().getStringExtra("Contact");
        String Address = getIntent().getStringExtra("Address");
        hostelLocation = getIntent().getStringExtra("Location");

        // Set data to UI
        binding.nmHostel.setText(hostel);
        binding.contact.setText(Contact);
        binding.addHostel.setText(Address);
        Glide.with(this).load(img).into(binding.hostelImg);

        // Gender
        binding.gender.setText(getIntent().getStringExtra("Gender"));

        // Handle Rent
        String rentStr = getIntent().getStringExtra("Rent");
        if (rentStr != null && !rentStr.isEmpty()) {
            binding.rent.setText(rentStr);
        } else {
            binding.rent.setText("N/A");
        }

        // Handle Capacity
        String capacityStr = getIntent().getStringExtra("Capacity");
        if (capacityStr != null && !capacityStr.isEmpty()) {
            binding.capacity.setText(capacityStr);
        } else {
            binding.capacity.setText("N/A");
        }

        // On Image Click - open fullscreen
        binding.hostelImg.setOnClickListener(view -> {
            Intent intent = new Intent(DetailsActivity.this, ImageShowing.class);
            intent.putExtra("imgsend", img);
            startActivity(intent);
        });

        // When user clicks “Click Here”
        binding.clickHereText.setOnClickListener(v -> {
            userClickedLocation = true;
            if (!isGPSEnabled()) {
                Toast.makeText(this, "Please turn on GPS to continue", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_REQUEST_CODE);
            } else {
                fetchLocationAndOpenMap();
            }
        });
    }

    private void fetchLocationAndOpenMap() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
                return;
            }

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = location.getLatitude() + "," + location.getLongitude();
                            openMap();
                        } else {
                            Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location permission required.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap() {
        if (currentLocation.isEmpty() || hostelLocation == null || hostelLocation.isEmpty()) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("https://www.google.com/maps/dir/" + currentLocation + "/" + Uri.encode(hostelLocation));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If user turned ON GPS manually after being asked, then continue to map
        if (userClickedLocation && isGPSEnabled()) {
            fetchLocationAndOpenMap();
            userClickedLocation = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndOpenMap();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
