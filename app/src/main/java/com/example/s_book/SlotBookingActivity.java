package com.example.s_book;

import android.os.Bundle;
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
                    // 3. Attach the data to the Adapter
                    adapter = new SlotAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Slot>> call, Throwable t) {
                Toast.makeText(SlotBookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}