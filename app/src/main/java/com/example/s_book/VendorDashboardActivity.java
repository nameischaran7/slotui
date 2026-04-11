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

        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
        long vendorId = pref.getLong("id", -1); // Get the Vendor's ID saved during login
        String name = pref.getString("name", "Vendor");

        TextView welcomeText = findViewById(R.id.vendorWelcomeText);
        welcomeText.setText("Dashboard: " + name);

        recyclerView = findViewById(R.id.vendorSlotsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (vendorId != -1) {
            fetchMySlots(vendorId);
        }
        Button logoutBtn = findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(v -> {
            // 1. Clear session
            getSharedPreferences("SBook_Prefs", MODE_PRIVATE).edit().clear().apply();

            // 2. Clear activity stack and go to Login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(this, "Logged out, mowa!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void fetchMySlots(long vendorId) {
        // Reuse your Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slotbooking-ytuf.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
}