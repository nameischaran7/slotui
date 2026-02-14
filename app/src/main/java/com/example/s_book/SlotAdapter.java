package com.example.s_book;

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

    public SlotAdapter(List<Slot> slotList) { this.slotList = slotList; }

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
        // Format the time string (e.g., "2026-02-13T09:00:00" -> "09:00 AM")
        holder.timeText.setText(slot.getStartTime().substring(11, 16));

        if (slot.isBooked()) {
            holder.bookBtn.setText("Occupied");
            holder.bookBtn.setEnabled(false);
        } else {
            holder.bookBtn.setOnClickListener(v -> {
                // We'll add the booking logic here next!
                Toast.makeText(v.getContext(), "Booking...", Toast.LENGTH_SHORT).show();
            });
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