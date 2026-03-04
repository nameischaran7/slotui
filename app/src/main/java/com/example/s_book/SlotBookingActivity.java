package com.example.s_book;
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
    }

    private void fetchSlots(long vendorId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8010/")
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
                .baseUrl("http://10.0.2.2:8010/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        // Get the name from SharedPreferences
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
        String currentUserName = pref.getString("name", "Unknown User");

        apiService.bookSlot(slotId, currentUserName).enqueue(new Callback<Slot>() {
            @Override
            public void onResponse(Call<Slot> call, Response<Slot> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SlotBookingActivity.this, "Booking Success!", Toast.LENGTH_SHORT).show();
                    // This refreshes the list so the button turns into "Occupied"
                    fetchSlots(getIntent().getLongExtra("VENDOR_ID", -1));
                }
            }
            @Override
            public void onFailure(Call<Slot> call, Throwable t) {
                Toast.makeText(SlotBookingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}