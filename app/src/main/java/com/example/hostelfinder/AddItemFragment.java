
package com.example.hostelfinder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.hostelfinder.Recycler.Model;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddItemFragment extends Fragment {

    private static final int IMAGE_REQUEST_CODE = 1;

    private EditText etName, etCapacity, etRent, etContact;



    private Button btnAddHostel, btnUploadImage;


    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private AutoCompleteTextView homeadd, areaadd, cityadd;

    private Uri selectedImageUri;
    private String uploadedImageUrl;

    static private int cnt = 7;

    static String[] Gender={"Girls","Boys"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    static String fulladdress;
    String g;
    String etadress;
     AutoCompleteTextView searchLocation;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);





        // Initialize views
        etName = view.findViewById(R.id.et_name);
        etCapacity = view.findViewById(R.id.et_capacity);
        etRent = view.findViewById(R.id.et_rent);
        etContact = view.findViewById(R.id.et_contact);
        searchLocation = view.findViewById(R.id.search);
        btnAddHostel = view.findViewById(R.id.btn_add_hostel);
        btnUploadImage = view.findViewById(R.id.btn_upload_image);
        autoCompleteTextView = view.findViewById(R.id.auto_complete_txt);



//        //search location
//        searchLocation.setAdapter(new PlaceAdapter(getContext(), android.R.layout.simple_list_item_1));


        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Set button click listeners
        btnUploadImage.setOnClickListener(v -> selectImage());
        btnAddHostel.setOnClickListener(v -> addHostel());

        // Other initializations


        arrayAdapter = new ArrayAdapter<>(requireContext(),R.layout.list_items,Gender);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                g = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(requireContext(), g, Toast.LENGTH_SHORT).show();
            }
        });
        // Set the search location dialog on click
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_location, null);

                AutoCompleteTextView homeadd = dialogView.findViewById(R.id.homeaddline);
                homeadd.setHorizontallyScrolling(true);
                homeadd.setSingleLine(true);

                AutoCompleteTextView areaadd = dialogView.findViewById(R.id.areaaddline);
                areaadd.setHorizontallyScrolling(true);
                areaadd.setSingleLine(true);

                AutoCompleteTextView cityadd = dialogView.findViewById(R.id.cityaddline);
                cityadd.setHorizontallyScrolling(true);
                cityadd.setSingleLine(true);

                AutoCompleteTextView pincode = dialogView.findViewById(R.id.pincode);

                // Attach TextWatcher to each field to remove error icons while typing
                addTextWatcher(homeadd);
                addTextWatcher(areaadd);
                addTextWatcher(cityadd);
                addTextWatcher(pincode);



                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btn_OK).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Validate fields inside the dialog box
                        boolean isHomeAddValid = validateField(homeadd);
                        boolean isAreaAddValid = validateField(areaadd);
                        boolean isCityAddValid = validateField(cityadd);
                        boolean isPincodeValid = validateField(pincode);

                        if (isHomeAddValid && isAreaAddValid && isCityAddValid && isPincodeValid) {


                            // If all fields are valid, combine them into the full address
                            fulladdress = homeadd.getText().toString().trim() + "," +
                                    areaadd.getText().toString().trim() + "," +
                                    cityadd.getText().toString().trim() + "," +
                                    pincode.getText().toString().trim();

                            // Update the searchLocation field with the full address
                            searchLocation.setText(fulladdress);
                            dialog.dismiss(); // Close dialog after validation
                        }

                    }
                });
                dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });









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
        String address = searchLocation.getText().toString().trim();
        String gender = g;
        String capacityStr = etCapacity.getText().toString().trim();
        String rentStr = etRent.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String location = searchLocation.getText().toString().trim();

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
        etCapacity.setText("");
        etRent.setText("");
        etContact.setText("");
        searchLocation.setText("");
        uploadedImageUrl = null;
        selectedImageUri = null;
    }
    // Boolean method to check the pattern
    private boolean isValidPattern(String input) {
        // Define the pattern for valid input (alphabets, numbers, commas, apostrophes, and spaces)
        String pattern = "^[a-zA-Z0-9 ,']+$";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(input);
        return matcher.matches();  // Return true if input matches the pattern
    }
    // Validate the fields only for the dialog
    private boolean validateField(AutoCompleteTextView field) {
//        String input = field.getText().toString().trim();
//        if (input.isEmpty()) {
//            // Show the error drawable if the field is empty
//            Drawable errorIcon = getResources().getDrawable(R.drawable.baseline_error_outline_24);
//            field.setCompoundDrawablesWithIntrinsicBounds(null, null, errorIcon, null);
//            field.setError("This field is required!"); // Optional error text
//            return false;
//
//        } else {
//            // Remove the error drawable when the field is filled
//            field.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//            return true;
//        }
        String input = field.getText().toString().trim();
        if(input.isEmpty()){
            // Show the error drawable in red color if the field is empty or doesn't match the pattern
            Drawable errorIcon = getResources().getDrawable(R.drawable.baseline_error_outline_24);
            errorIcon.setTint(Color.RED);  // Set the error icon color to red
            field.setCompoundDrawablesWithIntrinsicBounds(null, null, errorIcon, null);
            field.setError("Fill field!"); // Optional error text
            return false;

        }else if(!isValidPattern(input)){
            // Show the error drawable in red color if the field is empty or doesn't match the pattern
            Drawable errorIcon = getResources().getDrawable(R.drawable.baseline_error_outline_24);
            errorIcon.setTint(Color.RED);  // Set the error icon color to red
            field.setCompoundDrawablesWithIntrinsicBounds(null, null, errorIcon, null);
            field.setError("Invalid input!"); // Optional error text
            return false;
        }
        else {
            // Remove the error drawable when the field is valid
            field.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return true;
        }
    }
    private void addTextWatcher(AutoCompleteTextView field) {
        field.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove error icon and message while typing
                field.setError(null);
                field.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }








}

