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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SlotBookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SlotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);

        // 1. Setup UI
        String vendorName = getIntent().getStringExtra("VENDOR_NAME");
        long vendorId = getIntent().getLongExtra("VENDOR_ID", -1);

        TextView title = findViewById(R.id.slotTitle);
        title.setText("Book Slots for: " + vendorName);

        recyclerView = findViewById(R.id.slotsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Fetch data if ID is valid
        if (vendorId != -1) {
            fetchSlots(vendorId);
        } else {
            Toast.makeText(this, "Invalid Vendor ID", Toast.LENGTH_SHORT).show();
        }
        // Inside onCreate
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


    private void fetchSlots(long vendorId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slotbooking-ytuf.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getSlotsByVendor(vendorId).enqueue(new Callback<List<Slot>>() {
            @Override
            public void onResponse(Call<List<Slot>> call, Response<List<Slot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pass the list AND a listener to the adapter
                    adapter = new SlotAdapter(response.body(), slot -> {
                        // Check if ID is null before calling the method
                        if (slot != null && slot.getId() != null) {
                            bookSelectedSlot(slot.getId());
                        } else {
                            Toast.makeText(SlotBookingActivity.this, "Error: Slot ID is missing!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Slot>> call, Throwable t) {
                Toast.makeText(SlotBookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    // Ensure it looks exactly like this:
    private void bookSelectedSlot(long slotId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slotbooking-ytuf.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        // 1. Get the User ID from SharedPreferences
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
        long currentUserId = pref.getLong("userId", -1); // Use the key we synced earlier

        // 2. Create a User object to send as the body
        User userRequest = new User();
        userRequest.setId(currentUserId);

        // 3. Update the API call to pass the User object
        apiService.bookSlot(slotId, userRequest).enqueue(new Callback<Slot>() {
            @Override
            public void onResponse(Call<Slot> call, Response<Slot> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SlotBookingActivity.this, "Booking Success, Mowa!", Toast.LENGTH_SHORT).show();
                    fetchSlots(getIntent().getLongExtra("VENDOR_ID", -1));
                } else {
                    // If it fails, show the error code (likely 400 or 500)
                    Toast.makeText(SlotBookingActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Slot> call, Throwable t) {
                Toast.makeText(SlotBookingActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}