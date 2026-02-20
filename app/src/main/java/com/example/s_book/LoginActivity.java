package com.example.s_book;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText emailIn, passIn;
    Button loginBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void performLogin(User user) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.loginUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User loggedInUser = response.body();

                    // Save Session, mowa!
                    SharedPreferences pref = getSharedPreferences("SBook_Prefs", MODE_PRIVATE);
                    pref.edit().putLong("userId", loggedInUser.getId()).apply();
                    pref.edit().putString("userName", loggedInUser.getName()).apply();
                    pref.edit().putBoolean("isLoggedIn", true).apply();

                    Toast.makeText(LoginActivity.this, "Welcome " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();

                    // Go to MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Server Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}