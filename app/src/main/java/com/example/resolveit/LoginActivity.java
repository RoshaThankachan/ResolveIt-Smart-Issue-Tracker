package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private Button btnDemoUser, btnDemoAdmin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnDemoUser = findViewById(R.id.btnDemoUser);
        btnDemoAdmin = findViewById(R.id.btnDemoAdmin);

        databaseHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Demo autofill buttons
        btnDemoUser.setOnClickListener(v -> {
            etEmail.setText("alex@example.com");
            etPassword.setText("user123");
        });

        btnDemoAdmin.setOnClickListener(v -> {
            etEmail.setText("admin@resolveit.com");
            etPassword.setText("admin123");
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = databaseHelper.loginUser(email, password);

        if (user != null) {
            Toast.makeText(this, "Welcome, " + user.getName(), Toast.LENGTH_SHORT).show();

            if (user.isAdmin()) {
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                intent.putExtra("user_id", user.getUserId());
                intent.putExtra("user_name", user.getName());
                intent.putExtra("user_email", user.getEmail());
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                intent.putExtra("user_id", user.getUserId());
                intent.putExtra("user_name", user.getName());
                intent.putExtra("user_email", user.getEmail());
                startActivity(intent);
            }
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}