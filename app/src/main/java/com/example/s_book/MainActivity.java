package com.example.s_book;

import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VendorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.vendorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchVendors();
    }

    private void fetchVendors() {
        // Use 10.0.2.2 for Emulator or your Laptop IP for Real Phone
        String BASE_URL = "http://10.0.2.2:8010/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getVendors().enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pass the list from Spring Boot to the Adapter
                    adapter = new VendorAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Vendor>> call, Throwable t) {
                // If you see this, check if Spring Boot is running!
                Toast.makeText(MainActivity.this, "Network Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}