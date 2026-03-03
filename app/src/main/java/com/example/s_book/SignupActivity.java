package com.example.s_book;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
    }

    private void performSignup(String name, String email, String pass, String role) {
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
    }
}