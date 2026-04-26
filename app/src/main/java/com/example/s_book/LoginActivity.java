package com.example.s_book;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText emailIn, passIn;
    Button loginBtn;
    TextView signupRedirect;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);

        if (pref.getBoolean("isLoggedIn", false)) {
            // If already logged in, redirect based on role
            String role = pref.getString("role", "");
            Intent intent;
            if ("VENDOR".equalsIgnoreCase(role)) {
                intent = new Intent(this, VendorDashboardActivity.class);
            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivity(intent);
            finish(); // Close LoginActivity so they can't go back to it
            return;
        }
        setContentView(R.layout.activity_login);

        emailIn = findViewById(R.id.emailInput);
        passIn = findViewById(R.id.passInput);
        loginBtn = findViewById(R.id.loginButton);
        loginBtn.setOnClickListener(v -> {
            String email = emailIn.getText().toString();
            String password = passIn.getText().toString();

            User loginRequest = new User();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            performLogin(loginRequest);
        });
        signupRedirect = findViewById(R.id.signupRedirect);
        signupRedirect.setOnClickListener(v -> {
            // Navigate from Login to Signup
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin(User user) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.loginUser(user).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User loggedInUser = response.body();

                    // Check if ID is null to prevent the longValue() crash
                    if (loggedInUser.getId() == null) {
                        Toast.makeText(LoginActivity.this, "Error: User ID is null in database!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();

                    // Use the 'editor' variable for everything
                    editor.putLong("userId", loggedInUser.getId());
                    editor.putString("name", loggedInUser.getName());
                    editor.putString("role", loggedInUser.getRole());
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply(); // Apply everything at once

                    Toast.makeText(LoginActivity.this, "Welcome " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();

                    if ("VENDOR".equalsIgnoreCase(loggedInUser.getRole())) {
                        startActivity(new Intent(LoginActivity.this, VendorDashboardActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials, mowa!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Server Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}