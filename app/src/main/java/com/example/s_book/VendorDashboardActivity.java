package com.example.s_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VendorDashboardActivity extends AppCompatActivity {

    // Inside VendorDashboardActivity.java
    private RecyclerView recyclerView;
    private SlotAdapter adapter; // Reuse the SlotAdapter!


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        // 1. Get Vendor Details from SharedPreferences
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
        // FIX: Using "userId" to match your LoginActivity key
        final long vendorId = pref.getLong("userId", -1);
        String name = pref.getString("name", "Vendor");

        // 2. Set Welcome Text
        TextView welcomeText = findViewById(R.id.vendorWelcomeText);
        welcomeText.setText("Dashboard: " + name);

        // 3. Initialize RecyclerView
        recyclerView = findViewById(R.id.vendorSlotsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 4. Global Refresh Button Logic
        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            if (vendorId != -1) {
                fetchMySlots(vendorId);
                Toast.makeText(this, "Fetching latest slots...", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Floating Action Button for QR Scanning
        com.google.android.material.floatingactionbutton.FloatingActionButton fabScan = findViewById(R.id.btnScanQR);
        fabScan.setOnClickListener(v -> {
            com.journeyapps.barcodescanner.ScanOptions options = new com.journeyapps.barcodescanner.ScanOptions();
            options.setPrompt("Scan Customer's Booking QR");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            barcodeLauncher.launch(options);
        });

        // Initial data fetch
        if (vendorId != -1) {
            fetchMySlots(vendorId);
        }

        // 6. Logout Logic
        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(v -> {
            getSharedPreferences("SBook_Prefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // Ensure this launcher is defined outside onCreate (at the class level)
    private final androidx.activity.result.ActivityResultLauncher<com.journeyapps.barcodescanner.ScanOptions> barcodeLauncher =
            registerForActivityResult(new com.journeyapps.barcodescanner.ScanContract(), result -> {
                if(result.getContents() != null) {
                    handleScannedData(result.getContents());
                }
            });

    private void fetchMySlots(long vendorId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.getSlotsByVendor(vendorId).enqueue(new Callback<List<Slot>>() {

            public void onResponse(Call<List<Slot>> call, Response<List<Slot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Goal 2: Show slots to the Vendor. Pass 'null' for the listener
                    // because Vendors don't "Book" their own slots.
                    adapter = new SlotAdapter(response.body(), null);
                    recyclerView.setAdapter(adapter);
                }
            }
            public void onFailure(Call<List<Slot>> call, Throwable t) {
                Toast.makeText(VendorDashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleScannedData(String data) {
        // Data will look like "SLOT_ID_10"
        try {
            if (data.contains("SLOT_ID_")) {
                String idStr = data.replace("SLOT_ID_", "");
                long slotId = Long.parseLong(idStr);

                // 1. Call your API to verify this check-in
                verifyCheckInOnServer(slotId);
            } else {
                Toast.makeText(this, "Invalid QR Code, mowa!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Parsing Error!", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyCheckInOnServer(long slotId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        apiService.verifyCheckIn(slotId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Success! The slot is now false (Available) in Aiven DB
                    Toast.makeText(VendorDashboardActivity.this, "Slot Verified & Released!", Toast.LENGTH_LONG).show();

                    // REFRESH the list so the card updates immediately
                    SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
                    long vId = pref.getLong("userId", -1);
                    fetchMySlots(vId);
                } else {
                    Toast.makeText(VendorDashboardActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(VendorDashboardActivity.this, "Check-in failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}