package com.example.hostelfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String hostel = getIntent().getStringExtra("Name_Of_Hostel");
        binding.nmHostel.setText(hostel);

        String img = getIntent().getStringExtra("img");
        Glide.with(this).load(img).into(binding.hostelImg);

        String Contact = getIntent().getStringExtra("Contact");
        binding.contact.setText(Contact);

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

    }
}