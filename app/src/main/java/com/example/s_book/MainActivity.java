package com.example.s_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        // Check if the user is actually logged in first
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // If NOT logged in, then go to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return; // Stop executing the rest of this method
        }

        // If they ARE logged in, show the turf list
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        recyclerView = findViewById(R.id.vendorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchVendors();
        EditText searchBar = findViewById(R.id.searchEditText);

        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                // If user clears the search, fetch all vendors again
                if (query.isEmpty()) {
                    fetchVendors();
                } else {
                    // Otherwise, hit the new search endpoint in your Spring Boot app
                    performSearch(query);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }
    private void performSearch(String query) {
        // 1. Get your API Service (Ensure your RetrofitClient uses your PC's real IP!)
        String BASE_URL = "https://slotbooking-ytuf.onrender.com";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 2. Call the search endpoint we created in Spring Boot
        apiService.searchVendors(query).enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 3. Update the adapter with the filtered results!
                    adapter.updateList(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Vendor>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Search error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override // Add this line
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override // Add this line
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_my_bookings) {
            startActivity(new Intent(this, MyBookingsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        // 1. Clear the saved User ID and Session data
        SharedPreferences.Editor editor = getSharedPreferences("SBook_Prefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();

        // 2. Show a quick message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // 3. Redirect to Login Screen
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears activity stack
        startActivity(intent);
        finish(); // Closes MainActivity
    }

    private void fetchVendors() {
        // Use 10.0.2.2 for Emulator or your Laptop IP for Real Phone
        String BASE_URL = "https://slotbooking-ytuf.onrender.com";

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