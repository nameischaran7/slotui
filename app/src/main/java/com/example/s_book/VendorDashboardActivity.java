package com.example.s_book;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VendorDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        TextView welcomeText = findViewById(R.id.vendorWelcomeText);

        // Retrieve the name we saved during Login
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
        String name = pref.getString("userName", "Vendor");

        welcomeText.setText("Welcome to Vendor Panel, " + name);
    }
}