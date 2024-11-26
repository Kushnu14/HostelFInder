//package com.example.hostelfinder;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.hostelfinder.Recycler.Model;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class AddItemFragment extends Fragment {
//
//    private EditText etName, etAddress, etGender, etCapacity, etRent, etContact, etLocation;
//    private Button btnAddHostel;
//
//    private FirebaseFirestore db;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
//
//        // Initialize views
//        etName = view.findViewById(R.id.et_name);
//        etAddress = view.findViewById(R.id.et_address);
//        etGender = view.findViewById(R.id.et_gender);
//        etCapacity = view.findViewById(R.id.et_capacity);
//        etRent = view.findViewById(R.id.et_rent);
//        etContact = view.findViewById(R.id.et_contact);
//        etLocation = view.findViewById(R.id.et_location);
//        btnAddHostel = view.findViewById(R.id.btn_add_hostel);
//
//        // Initialize Firestore
//        db = FirebaseFirestore.getInstance();
//
//        // Set button click listener
//        btnAddHostel.setOnClickListener(v -> addHostel());
//
//        return view;
//    }
//
//    private void addHostel() {
//        // Get input values
//        String name = etName.getText().toString().trim();
//        String address = etAddress.getText().toString().trim();
//        String gender = etGender.getText().toString().trim();
//        String capacityStr = etCapacity.getText().toString().trim();
//        String rentStr = etRent.getText().toString().trim();
//        String contact = etContact.getText().toString().trim();
//        String location = etLocation.getText().toString().trim();
//
//        // Validate input
//        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(gender) ||
//                TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(rentStr) ||
//                TextUtils.isEmpty(contact) || TextUtils.isEmpty(location)) {
//            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Integer capacity;
//        Double rent;
//        try {
//            capacity = Integer.parseInt(capacityStr);
//            rent = Double.parseDouble(rentStr);
//        } catch (NumberFormatException e) {
//            Toast.makeText(getContext(), "Invalid capacity or rent format!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a new Model object
//        Model hostel = new Model("", name, address, gender, capacity, rent, contact, location);
//
//        // Add data to Firestore
//        db.collection("HostelData")
//                .add(hostel)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getContext(), "Hostel added successfully!", Toast.LENGTH_SHORT).show();
//                    clearFields();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Error adding hostel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void clearFields() {
//        etName.setText("");
//        etAddress.setText("");
//        etGender.setText("");
//        etCapacity.setText("");
//        etRent.setText("");
//        etContact.setText("");
//        etLocation.setText("");
//    }
//}
package com.example.hostelfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hostelfinder.Recycler.Model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddItemFragment extends Fragment {

    private static final int IMAGE_REQUEST_CODE = 1;

    private EditText etName, etAddress, etGender, etCapacity, etRent, etContact, etLocation;
    private Button btnAddHostel, btnUploadImage;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri selectedImageUri;
    private String uploadedImageUrl;
    static private int cnt = 7;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Initialize views
        etName = view.findViewById(R.id.et_name);
        etAddress = view.findViewById(R.id.et_address);
        etGender = view.findViewById(R.id.et_gender);
        etCapacity = view.findViewById(R.id.et_capacity);
        etRent = view.findViewById(R.id.et_rent);
        etContact = view.findViewById(R.id.et_contact);
        etLocation = view.findViewById(R.id.et_location);
        btnAddHostel = view.findViewById(R.id.btn_add_hostel);
        btnUploadImage = view.findViewById(R.id.btn_upload_image);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Set button click listeners
        btnUploadImage.setOnClickListener(v -> selectImage());
        btnAddHostel.setOnClickListener(v -> addHostel());

        return view;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            String imageFileName = "hostels/" + UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child(imageFileName);

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                uploadedImageUrl = uri.toString();
                                Toast.makeText(getContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void addHostel() {
        // Get input values
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String capacityStr = etCapacity.getText().toString().trim();
        String rentStr = etRent.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(capacityStr) || TextUtils.isEmpty(rentStr) ||
                TextUtils.isEmpty(contact) || TextUtils.isEmpty(location)) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uploadedImageUrl == null) {
            Toast.makeText(getContext(), "Please upload an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer capacity;
        Double rent;
        try {
            capacity = Integer.parseInt(capacityStr);
            rent = Double.parseDouble(rentStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid capacity or rent format!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Generate a unique ID (or use an input field for ID)

        String docname = "Hostel" + cnt;
        String id = String.valueOf(cnt);

        // Create a new Model object
        Model hostel = new Model(id, name, address, gender, capacity, rent, contact, location, uploadedImageUrl);

        // Add data to Firestore
        db.collection("HostelData")
                .document(docname) // Set the document name to the custom ID
                .set(hostel)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Hostel added successfully!", Toast.LENGTH_SHORT).show();
                    cnt++;
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error adding hostel: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void clearFields() {
        etName.setText("");
        etAddress.setText("");
        etGender.setText("");
        etCapacity.setText("");
        etRent.setText("");
        etContact.setText("");
        etLocation.setText("");
        uploadedImageUrl = null;
        selectedImageUri = null;
    }
}

