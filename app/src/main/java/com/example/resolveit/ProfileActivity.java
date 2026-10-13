package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePic;
    private TextView tvProfileName, tvProfileRole, tvProfileEmail, tvProfilePhone, tvProfileDepartment;
    private TextView tvStatTotal, tvStatPending, tvStatResolved;
    private Button btnEditProfile;

    private DatabaseHelper databaseHelper;
    private int userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileDepartment = findViewById(R.id.tvProfileDepartment);

        tvStatTotal = findViewById(R.id.tvStatTotal);
        tvStatPending = findViewById(R.id.tvStatPending);
        tvStatResolved = findViewById(R.id.tvStatResolved);

        btnEditProfile = findViewById(R.id.btnEditProfile);

        databaseHelper = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("user_id", -1);
        String email = getIntent().getStringExtra("user_email");

        if (userId == -1 && email != null) {
            User u = databaseHelper.getUserByEmail(email);
            if (u != null) userId = u.getUserId();
        }

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        loadProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }

    private void loadProfileData() {
        if (userId != -1) {
            currentUser = databaseHelper.getUserById(userId);
        }

        if (currentUser != null) {
            tvProfileName.setText(currentUser.getName());
            tvProfileEmail.setText(currentUser.getEmail());
            tvProfilePhone.setText(currentUser.getPhone());
            tvProfileDepartment.setText(currentUser.getDepartment());
            tvProfileRole.setText(currentUser.getRole());

            int[] counts = databaseHelper.getIssueCounts(currentUser.getUserId());
            tvStatTotal.setText(String.valueOf(counts[0]));
            tvStatPending.setText(String.valueOf(counts[1]));
            tvStatResolved.setText(String.valueOf(counts[3]));
        }
    }

    private void showEditProfileDialog() {
        if (currentUser == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText etName = new EditText(this);
        etName.setHint("Name");
        etName.setText(currentUser.getName());
        layout.addView(etName);

        final EditText etPhone = new EditText(this);
        etPhone.setHint("Phone");
        etPhone.setText(currentUser.getPhone());
        layout.addView(etPhone);

        final EditText etDepartment = new EditText(this);
        etDepartment.setHint("Department / Class");
        etDepartment.setText(currentUser.getDepartment());
        layout.addView(etDepartment);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = etName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newDept = etDepartment.getText().toString().trim();

            if (newName.isEmpty() || newPhone.isEmpty() || newDept.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = databaseHelper.updateUserProfile(currentUser.getUserId(), newName, newPhone, newDept);
            if (updated) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                loadProfileData();
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_profile) {
            Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_my_issues) {
            Intent intent = new Intent(this, MyIssuesActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            loadProfileData();
            Toast.makeText(this, "Profile refreshed", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
