package com.example.s_book;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {
    private List<Slot> slotList;
    private OnSlotClickListener listener;
    public interface OnSlotClickListener {
        void onBookClick(Slot slot);
    }

    // Update constructor to take the listener
    public SlotAdapter(List<Slot> slotList, OnSlotClickListener listener) {
        this.slotList = slotList;
        this.listener = listener;
    }

    @NonNull
    @Override

    public SlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 1. Ensure the layout name matches your XML file exactly
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot, parent, false);

        // 2. You MUST return a new instance of your ViewHolder
        return new SlotViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot slot = slotList.get(position);
        if (slot.getStartTime() != null) {
            // If the string is "2026-03-05 10:00:00", substring(11, 16) gives "10:00"
            String fullTime = slot.getStartTime();
            if (fullTime.length() > 16) {
                holder.timeText.setText(fullTime.substring(11, 16));
            } else {
                holder.timeText.setText(fullTime);
            }
        }
        // 1. Force Visibility First
        holder.bookBtn.setVisibility(View.VISIBLE);
        holder.bookBtn.setAlpha(1.0f); // Ensure it's not transparent

        if (listener == null) { // Vendor Side
            holder.bookBtn.setEnabled(false);
            if (slot.isBooked()) {
                // Show the name saved in the database
                String bookedBy = slot.getBookedByName() != null ? slot.getBookedByName() : "User";
                holder.bookBtn.setText("By: " + bookedBy);
                holder.bookBtn.setBackgroundColor(Color.RED);
            } else {
                holder.bookBtn.setText("Available");
                holder.bookBtn.setBackgroundColor(Color.GREEN);
            }
        } else {
            // USER SIDE
            if (slot.isBooked()) {
                holder.bookBtn.setText("Occupied");
                holder.bookBtn.setEnabled(false);
                holder.bookBtn.setBackgroundColor(Color.LTGRAY);
            } else {
                holder.bookBtn.setEnabled(true);
                holder.bookBtn.setText("Book Now");
                holder.bookBtn.setBackgroundColor(Color.BLUE);
                holder.bookBtn.setOnClickListener(v -> listener.onBookClick(slot));
            }
        }
    }
    @Override
    public int getItemCount() { return slotList.size(); }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        Button bookBtn;
        public SlotViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.slotTime);
            bookBtn = itemView.findViewById(R.id.bookButton);

        }
    }
}