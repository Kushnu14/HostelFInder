package com.example.hostelfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.hostelfinder.Recycler.Adapter;
import com.example.hostelfinder.Recycler.Model;
import com.example.hostelfinder.databinding.ActivityMainScreenBinding;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class MainScreen extends AppCompatActivity {

    ActivityMainScreenBinding binding;
    FirebaseFirestore firestore;
    ArrayList<Model> hostelList;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        hostelList = new ArrayList<>();
        adapter = new Adapter(this,hostelList);
        binding.rcv.setAdapter(adapter);
        binding.rcv.setLayoutManager(new LinearLayoutManager(this));

        LoadData();

        binding.searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterList(editable.toString());

            }
        });

    }

    private void filterList(String text) {
        java.util.List<Model> filteredList =new ArrayList<>();
        String lowerText = text.toLowerCase();

        for(Model item: hostelList){
            String rentString = String.valueOf(item.getRent());
            String capacityString = String.valueOf(item.getCapacity());
            if(item.getName_Of_Hostel().toLowerCase().contains(lowerText)||
                    item.getLocation().toLowerCase().contains(lowerText) ||
                    item.getAddress().toLowerCase().contains(lowerText) ||
                    item.getGender().toLowerCase().contains(lowerText) ||
                    rentString.toLowerCase().contains(lowerText) ||  // Compare rent as string
                    capacityString.toLowerCase().contains(lowerText)){
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()){
            binding.data.setVisibility(View.VISIBLE);
        }
        else {
            adapter.setFilteredList(filteredList);
            binding.data.setVisibility(View.GONE);
        }
    }

    private void LoadData() {
        hostelList.clear();
        firestore.collection("HostelData").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
                        return;
                    }
        for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
            if(dc.getType() == DocumentChange.Type.ADDED){
                hostelList.add(dc.getDocument().toObject(Model.class));
            }
            adapter.notifyDataSetChanged();
        }
                });

    }
}