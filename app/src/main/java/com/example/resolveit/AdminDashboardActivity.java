package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminWelcome, tvAdminEmail;
    private TextView tvAdminTotalCount, tvAdminPendingCount, tvAdminInProgressCount, tvAdminResolvedCount;
    private MaterialCardView cardTotalIssues, cardPendingIssues, cardInProgressIssues, cardResolvedIssues;
    private Button btnAdminViewAllIssues, btnAdminLogout;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        tvAdminEmail = findViewById(R.id.tvAdminEmail);
        tvAdminTotalCount = findViewById(R.id.tvAdminTotalCount);
        tvAdminPendingCount = findViewById(R.id.tvAdminPendingCount);
        tvAdminInProgressCount = findViewById(R.id.tvAdminInProgressCount);
        tvAdminResolvedCount = findViewById(R.id.tvAdminResolvedCount);

        cardTotalIssues = findViewById(R.id.cardTotalIssues);
        cardPendingIssues = findViewById(R.id.cardPendingIssues);
        cardInProgressIssues = findViewById(R.id.cardInProgressIssues);
        cardResolvedIssues = findViewById(R.id.cardResolvedIssues);

        btnAdminViewAllIssues = findViewById(R.id.btnAdminViewAllIssues);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        databaseHelper = new DatabaseHelper(this);

        String name = getIntent().getStringExtra("user_name");
        String email = getIntent().getStringExtra("user_email");

        if (name != null) {
            tvAdminWelcome.setText(name);
        }
        if (email != null) {
            tvAdminEmail.setText(email);
        }

        btnAdminViewAllIssues.setOnClickListener(v -> openAdminIssues("All"));
        cardTotalIssues.setOnClickListener(v -> openAdminIssues("All"));
        cardPendingIssues.setOnClickListener(v -> openAdminIssues("Pending"));
        cardInProgressIssues.setOnClickListener(v -> openAdminIssues("In Progress"));
        cardResolvedIssues.setOnClickListener(v -> openAdminIssues("Resolved"));

        btnAdminLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openAdminIssues(String initialStatus) {
        Intent intent = new Intent(AdminDashboardActivity.this, AdminIssuesActivity.class);
        intent.putExtra("initial_status", initialStatus);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        int[] counts = databaseHelper.getIssueCounts(null);
        tvAdminTotalCount.setText(String.valueOf(counts[0]));
        tvAdminPendingCount.setText(String.valueOf(counts[1]));
        tvAdminInProgressCount.setText(String.valueOf(counts[2]));
        tvAdminResolvedCount.setText(String.valueOf(counts[3]));
    }
}
