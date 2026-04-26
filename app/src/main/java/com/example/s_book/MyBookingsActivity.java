package com.example.s_book;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Slot> mySlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        // Enable Back Button in Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Bookings");
        }

        recyclerView = findViewById(R.id.bookingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the current User ID (stored during Login)
        // Change "user_prefs" to "SBook_Prefs"
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
// CHANGE: Use "userId" to match LoginActivity
        long vendorId = pref.getLong("userId", -1);
        String name = pref.getString("name", "Vendor");

        if (vendorId != -1) {
            fetchMyBookings(vendorId);
        } else {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMyBookings(long userId) {
        // Retrofit call to your Spring Boot endpoint
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://slotbooking-ytuf.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.getUserBookings(userId).enqueue(new Callback<List<Slot>>() {
            @Override
            public void onResponse(Call<List<Slot>> call, Response<List<Slot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mySlots = response.body();
                    adapter = new BookingAdapter(MyBookingsActivity.this, mySlots);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Slot>> call, Throwable t) {
                Toast.makeText(MyBookingsActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle the Back Button click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}