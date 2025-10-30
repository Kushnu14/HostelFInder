package com.example.hostelfinder;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.databinding.ActivityDetailsBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String currentLocation = "";
    private String hostelLocation = "";

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

        // Handle Rent
        String rentStr = getIntent().getStringExtra("Rent");
        if (rentStr != null && !rentStr.isEmpty()) {
            try {
                double rent = Double.parseDouble(rentStr);
                binding.rent.setText(String.valueOf(rent));
            } catch (NumberFormatException e) {
                binding.rent.setText("N/A");
            }
        } else {
            binding.rent.setText("N/A");
        }

        // Handle Capacity
        String capacityStr = getIntent().getStringExtra("Capacity");
        if (capacityStr != null && !capacityStr.isEmpty()) {
            try {
                int capacity = Integer.parseInt(capacityStr);
                binding.capacity.setText(String.valueOf(capacity));
            } catch (NumberFormatException e) {
                binding.capacity.setText("N/A");
            }
        } else {
            binding.capacity.setText("N/A");
        }

        // Gender
        String gender = getIntent().getStringExtra("Gender");
        binding.gender.setText(gender);

        // On Image Click - open fullscreen
        binding.hostelImg.setOnClickListener(view -> {
            Intent intent = new Intent(DetailsActivity.this, ImageShowing.class);
            intent.putExtra("imgsend", img);
            startActivity(intent);
        });

        // ================== LOCATION & MAP ==================
        binding.clickHereText.setOnClickListener(view -> {
            if (currentLocation.isEmpty()) {
                // Check permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                    return;
                }

                // Request high accuracy location
                LocationRequest locationRequest = new LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000
                ).build();

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                        .setAlwaysShow(true);

                SettingsClient settingsClient = LocationServices.getSettingsClient(this);
                settingsClient.checkLocationSettings(builder.build())
                        .addOnSuccessListener(locationSettingsResponse -> {
                            fetchLastLocation(); // GPS already on
                            Toast.makeText(this, "Fetching current location, please wait...", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            if (e instanceof ResolvableApiException) {
                                try {
                                    // Show GPS enable dialog only once
                                    SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                                    boolean shown = prefs.getBoolean("gps_dialog_shown", false);
                                    if (!shown) {
                                        ((ResolvableApiException) e).startResolutionForResult(this, 200);
                                        prefs.edit().putBoolean("gps_dialog_shown", true).apply();
                                    } else {
                                        Toast.makeText(this, "Please enable GPS to continue", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
            } else {
                // Open Google Maps with route
                openMapRoute();
            }
        });
    }

    // LOCATION CODE

    private void fetchLastLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
                return;
            }

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocation = location.getLatitude() + "," + location.getLongitude();
                            openMapRoute();
                        } else {
                            Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMapRoute() {
        if (currentLocation.isEmpty()) {
            Toast.makeText(this, "Location not available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hostelLocation == null || hostelLocation.isEmpty()) {
            Toast.makeText(this, "Hostel location not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("https://www.google.com/maps/dir/" + currentLocation + "/" + Uri.encode(hostelLocation));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLastLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle GPS dialog result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                fetchLastLocation();
            } else {
                Toast.makeText(this, "GPS is required to get location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
