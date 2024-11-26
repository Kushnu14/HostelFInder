package com.example.hostelfinder;

import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentContainer extends AppCompatActivity {


BottomNavigationView bnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        bnView = findViewById(R.id.bnView);

        bnView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    loadFrag(new MainScreenFragment(), false);
                } else if (id == R.id.nav_add) {
                    loadFrag(new AddItemFragment(), false);
                }

                return true;
            }
        });

        // Load the default fragment
        if (savedInstanceState == null) {
            bnView.setSelectedItemId(R.id.nav_home);
        }
    }

    public void loadFrag(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Always replace the current fragment
        ft.replace(R.id.container, fragment);

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.commit();
    }
}