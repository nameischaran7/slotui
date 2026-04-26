package com.example.s_book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Slot> bookings;

    public BookingAdapter(Context context, List<Slot> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {

        Slot slot = bookings.get(position);

        // 1. Get Vendor Name (Possible because of the @ManyToOne relationship)
        if (slot.getVendor() != null) {
            holder.tvVendorName.setText(slot.getVendor().getName());
            holder.tvLocation.setText(slot.getVendor().getLocation());
        }

        String[] parts = slot.getStartTime().split(" ");
        String datePart = parts[0];
        String timePart = parts[1];

        // Parse the hour
        int hour = Integer.parseInt(timePart.substring(0, 2));
        int nextHour = (hour + 1) % 24; // Handle midnight wrap-around

        // Format the next hour string (e.g., 13 -> 14)
        String nextHourStr = String.format("%02d", nextHour);
        String endTimeStr = nextHourStr + timePart.substring(2);
        // 2. Format and display the time
        // If your Spring Boot sends a string, it will show here.
        // You can use slot.getStartTime().toString() if needed.
        holder.tvTime.setText("Time: " + slot.getStartTime() + " to " + endTimeStr);

        holder.tvStatus.setText("Status: Confirmed");
        // Inside onBindViewHolder (at the bottom)
        holder.btnShowQR.setOnClickListener(v -> {
            android.app.Dialog qrDialog = new android.app.Dialog(context);
            qrDialog.setContentView(R.layout.dialog_qr_code);

            android.widget.ImageView qrImageView = qrDialog.findViewById(R.id.qrImageView);
            android.widget.Button btnClose = qrDialog.findViewById(R.id.btnCloseDialog);

            try {
                // We use the Slot ID as the data for the QR code
                com.journeyapps.barcodescanner.BarcodeEncoder barcodeEncoder = new com.journeyapps.barcodescanner.BarcodeEncoder();
                android.graphics.Bitmap bitmap = barcodeEncoder.encodeBitmap("SLOT_ID_" + slot.getId(),
                        com.google.zxing.BarcodeFormat.QR_CODE, 500, 500);
                qrImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                android.widget.Toast.makeText(context, "QR Error, mowa!", android.widget.Toast.LENGTH_SHORT).show();
            }

            btnClose.setOnClickListener(view -> qrDialog.dismiss());
            qrDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvTime, tvStatus, tvLocation;
        Button btnShowQR;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.bookingVendorName);
            tvTime = itemView.findViewById(R.id.bookingTime);
            tvStatus = itemView.findViewById(R.id.bookingStatus);
            tvLocation = itemView.findViewById(R.id.bookingLocation);
            btnShowQR = itemView.findViewById(R.id.btnShowQR);
        }
    }
}