package com.example.hostelfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hostelfinder.databinding.ActivityDetailsBinding;
import com.example.hostelfinder.databinding.ActivityImageShowingBinding;
import com.ortiz.touchview.TouchImageView;

public class ImageShowing extends AppCompatActivity {
    ActivityImageShowingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageShowingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String img = getIntent().getStringExtra("imgsend");
        Glide.with(this).load(img).into(binding.singleImage);

    }
}