package com.example.hostelfinder.Recycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelfinder.DetailsActivity;
import com.example.hostelfinder.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<Model> hostelList;

    public Adapter(Context context, ArrayList<Model> hostelList) {
        this.context = context;
        this.hostelList = hostelList;
    }
    public void setFilteredList(java.util.List<Model> filteredList){
        this.hostelList=(ArrayList<Model>) filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Model model = hostelList.get(position);
        holder.nmHostel.setText(model.getName_Of_Hostel());

        holder.itemView.setOnClickListener(v->{
            Intent intent =new Intent(context, DetailsActivity.class);
            intent.putExtra("Name_Of_Hostel",model.getName_Of_Hostel());
            intent.putExtra("Address",model.getAddress());
            intent.putExtra("Contact", model.getContact());
            intent.putExtra("Capacity", String.valueOf(model.getCapacity()));
            intent.putExtra("Rent", String.valueOf(model.getRent()));
            intent.putExtra("Gender",model.getGender());
            intent.putExtra("id",model.getId());
            intent.putExtra("img",model.getImg());
            intent.putExtra("Location",model.getLocation());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hostelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nmHostel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nmHostel=itemView.findViewById(R.id.nmHostel);
        }
    }
}
