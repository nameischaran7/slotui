package com.example.s_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    EditText nameIn, emailIn, passIn;
    RadioGroup roleGroup;
    Button signupBtn;
    LinearLayout vendorFields;
    EditText categoryIn, locationIn, priceIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        vendorFields = findViewById(R.id.vendorFieldsContainer);
        categoryIn = findViewById(R.id.etCategory);
        locationIn = findViewById(R.id.etLocation);
        priceIn = findViewById(R.id.etPrice);


        nameIn = findViewById(R.id.signupName);
        emailIn = findViewById(R.id.signupEmail);
        passIn = findViewById(R.id.signupPass);
        roleGroup = findViewById(R.id.roleGroup);
        signupBtn = findViewById(R.id.signupButton);

        signupBtn.setOnClickListener(v -> {
            int selectedId = roleGroup.getCheckedRadioButtonId();
            RadioButton selectedRole = findViewById(selectedId);

            if (selectedRole == null) {
                Toast.makeText(this, "Please select a role, mowa!", Toast.LENGTH_SHORT).show();
                return;
            }

            performSignup(
                    nameIn.getText().toString(),
                    emailIn.getText().toString(),
                    passIn.getText().toString(),
                    selectedRole.getText().toString().toUpperCase()
            );
        });
        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            if (rb != null && "VENDOR".equalsIgnoreCase(rb.getText().toString())) {
                vendorFields.setVisibility(android.view.View.VISIBLE);
            } else {
                vendorFields.setVisibility(android.view.View.GONE);
            }
        });
    }

    private void performSignup(String name, String email, String pass, String role) {
        if ("USER".equals(role)) {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(pass);
            newUser.setRole(role);

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            apiService.signupUser(newUser).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Signup Success! Login now.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Vendor newVendor = new Vendor();
            newVendor.setName(name);
            newVendor.setEmail(email);
            newVendor.setPassword(pass);
            newVendor.setRole(role);
            newVendor.setCategory(categoryIn.getText().toString());
            newVendor.setLocation(locationIn.getText().toString());

            // Parse price safely
            // Inside the else block for VENDOR
            String priceStr = priceIn.getText().toString().trim();
            if (!priceStr.isEmpty()) {
                try {
                    newVendor.setPricePerHour(Double.parseDouble(priceStr));
                } catch (NumberFormatException e) {
                    newVendor.setPricePerHour(0.0); // Safe fallback
                }
            } else {
                newVendor.setPricePerHour(0.0); // Mandatory for database not null constraints
            }
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
            apiService.signupVendor(newVendor).enqueue(new Callback<Vendor>() {
                @Override
                public void onResponse(Call<Vendor> call, Response<Vendor> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Signup Success!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        // Mowa, read this number on your screen!
                        Toast.makeText(SignupActivity.this, "Code: " + response.code(), Toast.LENGTH_LONG).show();
                        android.util.Log.e("RETROFIT_ERROR", "Body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Vendor> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}