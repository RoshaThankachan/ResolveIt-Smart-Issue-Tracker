package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class UserDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUserEmail;
    private TextView tvTotalCount, tvPendingCount, tvInProgressCount, tvResolvedCount;
    private Button btnReportIssue, btnMyIssues, btnProfile, btnLogout;

    private DatabaseHelper databaseHelper;
    private int userId;
    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvResolvedCount = findViewById(R.id.tvResolvedCount);

        btnReportIssue = findViewById(R.id.btnReportIssue);
        btnMyIssues = findViewById(R.id.btnMyIssues);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        databaseHelper = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("user_id", -1);
        userName = getIntent().getStringExtra("user_name");
        userEmail = getIntent().getStringExtra("user_email");

        if (userId == -1 && userEmail != null) {
            User user = databaseHelper.getUserByEmail(userEmail);
            if (user != null) {
                userId = user.getUserId();
                userName = user.getName();
            }
        }

        if (userName != null) {
            tvWelcome.setText("Welcome, " + userName + "!");
        }
        if (userEmail != null) {
            tvUserEmail.setText(userEmail);
        }

        // Action: Report an Issue
        btnReportIssue.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, ReportIssueActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });

        // Action: My Issues
        btnMyIssues.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, MyIssuesActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("user_name", userName);
            startActivity(intent);
        });

        // Action: Profile
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        });

        // Action: Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDataAndCounts();
    }

    private void loadUserDataAndCounts() {
        if (userId != -1) {
            User user = databaseHelper.getUserById(userId);
            if (user != null) {
                userName = user.getName();
                userEmail = user.getEmail();
                tvWelcome.setText("Welcome, " + userName + "!");
                tvUserEmail.setText(userEmail);
            }

            int[] counts = databaseHelper.getIssueCounts(userId);
            tvTotalCount.setText(String.valueOf(counts[0]));
            tvPendingCount.setText(String.valueOf(counts[1]));
            tvInProgressCount.setText(String.valueOf(counts[2]));
            tvResolvedCount.setText(String.valueOf(counts[3]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_my_issues) {
            Intent intent = new Intent(this, MyIssuesActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            loadUserDataAndCounts();
            Toast.makeText(this, "Dashboard refreshed", Toast.LENGTH_SHORT).show();
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
