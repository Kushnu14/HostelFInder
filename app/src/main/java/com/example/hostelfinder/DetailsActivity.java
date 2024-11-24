package com.example.hostelfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.databinding.ActivityDetailsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String currentLocation = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        String hostel = getIntent().getStringExtra("Name_Of_Hostel");
        binding.nmHostel.setText(hostel);

        String img = getIntent().getStringExtra("img");
        Glide.with(this).load(img).into(binding.hostelImg);

        String Contact = getIntent().getStringExtra("Contact");
        binding.contact.setText(Contact);

        String Location = getIntent().getStringExtra("Location");
        //binding.clickHereText.setText(Location);


        /*String capacity = getIntent().getStringExtra("Capacity");
        binding.capacity.setText(capacity);

        String Contact = getIntent().getStringExtra("Contact");
        binding.contact.setText(Contact);

        String rent = getIntent().getStringExtra("Rent");
        binding.addHostel.setText(rent);*/
        String address = getIntent().getStringExtra("Address");
        binding.addHostel.setText(address);

        // Inside onCreate() method of DetailsActivity

// Convert capacity from String to integer
        String capacityStr = getIntent().getStringExtra("Capacity");
        if (capacityStr != null && !capacityStr.isEmpty()) {
            try {
                Integer capacity = Integer.parseInt(capacityStr);
                binding.capacity.setText(String.valueOf(capacity));
            } catch (NumberFormatException e) {
                // Handle conversion error
                binding.capacity.setText("N/A");
                e.printStackTrace();
            }
        } else {
            binding.capacity.setText("N/A");
        }



// Convert rent from String to double
        String rentStr = getIntent().getStringExtra("Rent");
        if (rentStr != null && !rentStr.isEmpty()) {
            try {
                double rent = Double.parseDouble(rentStr);
                binding.rent.setText(String.valueOf(rent));
            } catch (NumberFormatException e) {
                // Handle conversion error
                binding.rent.setText("N/A");
                e.printStackTrace();
            }
        } else {
            binding.rent.setText("N/A");
        }


        String gender = getIntent().getStringExtra("Gender");
        binding.gender.setText(gender);
        binding.hostelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(DetailsActivity.this,ImageShowing.class);
                intent.putExtra("imgsend",img);
                startActivity(intent);

            }
        });
        getCurrentLocation();
        binding.clickHereText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocation.isEmpty()) {
                    Toast.makeText(DetailsActivity.this, "Fetching current location, please wait...", Toast.LENGTH_SHORT).show();
                    return;
                }





                // Open Google Maps with the route
                Uri uri = Uri.parse("https://www.google.com/maps/dir/" + currentLocation + "/" +  Uri.encode(Location) );
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location.getLatitude() + "," + location.getLongitude();
                } else {
                    Toast.makeText(DetailsActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
