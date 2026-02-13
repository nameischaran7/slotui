package com.example.s_book;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
    private List<Vendor> vendorList;

    public VendorAdapter(List<Vendor> vendorList) {
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        holder.name.setText(vendor.getName());
        holder.category.setText(vendor.getCategory());
        holder.location.setText(vendor.getLocation());
        holder.price.setText("₹" + vendor.getPricePerHour() + "/hr");
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, location, price;
        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vendorName);
            category = itemView.findViewById(R.id.vendorCategory);
            location = itemView.findViewById(R.id.vendorLocation);
            price = itemView.findViewById(R.id.vendorPrice);
        }
    }
}