//package com.example.hostelfinder;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Toast;
//
//import com.example.hostelfinder.Recycler.Adapter;
//import com.example.hostelfinder.Recycler.Model;
//import com.example.hostelfinder.databinding.ActivityMainScreenBinding;
//
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//public class MainScreen extends AppCompatActivity {
//
//    ActivityMainScreenBinding binding;
//    FirebaseFirestore firestore;
//    ArrayList<Model> hostelList;
//    Adapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding=ActivityMainScreenBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        firestore = FirebaseFirestore.getInstance();
//        hostelList = new ArrayList<>();
//        adapter = new Adapter(this,hostelList);
//        binding.rcv.setAdapter(adapter);
//        binding.rcv.setLayoutManager(new LinearLayoutManager(this));
//
//        LoadData();
//
//        binding.searchview.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                filterList(editable.toString());
//
//            }
//        });
//
//    }
//
//    private void filterList(String text) {
//        java.util.List<Model> filteredList =new ArrayList<>();
//        String lowerText = text.toLowerCase();
//
//        for(Model item: hostelList){
//            String rentString = String.valueOf(item.getRent());
//            String capacityString = String.valueOf(item.getCapacity());
//            if(item.getName_Of_Hostel().toLowerCase().contains(lowerText)||
//                    item.getLocation().toLowerCase().contains(lowerText) ||
//                    item.getAddress().toLowerCase().contains(lowerText) ||
//                    item.getGender().toLowerCase().contains(lowerText) ||
//                    rentString.toLowerCase().contains(lowerText) ||  // Compare rent as string
//                    capacityString.toLowerCase().contains(lowerText)){
//                filteredList.add(item);
//            }
//        }
//        if(filteredList.isEmpty()){
//            binding.data.setVisibility(View.VISIBLE);
//        }
//        else {
//            adapter.setFilteredList(filteredList);
//            binding.data.setVisibility(View.GONE);
//        }
//    }
//
//    private void LoadData() {
//        hostelList.clear();
//        firestore.collection("HostelData").orderBy("id", Query.Direction.ASCENDING)
//                .addSnapshotListener((value, error) -> {
//                    if(error != null){
//                        Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//        for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
//            if(dc.getType() == DocumentChange.Type.ADDED){
//                hostelList.add(dc.getDocument().toObject(Model.class));
//            }
//            adapter.notifyDataSetChanged();
//        }
//                });
//
//    }
//}
//public class MainScreenFragment extends Fragment{
//    public MainScreenFragment() {
//        // Required empty public constructor
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add_item, container, false);
//    }
//
//}
package com.example.hostelfinder;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hostelfinder.Recycler.Adapter;
import com.example.hostelfinder.Recycler.Model;
import com.example.hostelfinder.databinding.FragmentMainScreenBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainScreenFragment extends Fragment {

    private FragmentMainScreenBinding binding;
    private FirebaseFirestore firestore;
    private ArrayList<Model> hostelList;
    private Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentMainScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore and other variables
        firestore = FirebaseFirestore.getInstance();
        hostelList = new ArrayList<>();
        adapter = new Adapter(requireContext(), hostelList);

        // Setup RecyclerView
        binding.rcv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rcv.setAdapter(adapter);

        // Load Data
        loadData();

        // Setup SearchView listener
        binding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search action when user submits the query
                filterList(query);
                return false; // Return true if the query is handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle real-time text changes in the SearchView
                filterList(newText);
                return true;
            }
        });
    }
    private void filterList(String text) {
        List<Model> filteredList = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (Model item : hostelList) {
            String rentString = String.valueOf(item.getRent());
            String capacityString = String.valueOf(item.getCapacity());
            if (item.getName_Of_Hostel().toLowerCase().contains(lowerText) ||
                    item.getLocation().toLowerCase().contains(lowerText) ||
                    item.getAddress().toLowerCase().contains(lowerText) ||
                    item.getGender().toLowerCase().contains(lowerText) ||
                    rentString.contains(lowerText) ||
                    capacityString.contains(lowerText)) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            binding.data.setVisibility(View.VISIBLE); // Show "no results" message
        } else {
            adapter.setFilteredList(filteredList);
            binding.data.setVisibility(View.GONE); // Hide "no results" message
        }
    }

    private void loadData() {
        hostelList.clear();
        firestore.collection("HostelData").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(requireContext(), "Error Loading Data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                // Ensure the model class has proper data
                                Model model = dc.getDocument().toObject(Model.class);
                                hostelList.add(model);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Data is null", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
