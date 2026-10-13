package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueDetailsActivity extends AppCompatActivity {

    private TextView tvDetailIssueId, tvDetailTitle, tvDetailStatus, tvDetailPriority;
    private TextView tvDetailCategory, tvDetailDate, tvDetailReporter, tvDetailDescription;
    private TextView tvNoUpdates;
    private ImageView ivDetailImage;
    private LinearLayout layoutReporter;
    private RecyclerView rvIssueUpdates;
    private Button btnEditIssue, btnDeleteIssue;

    private DatabaseHelper databaseHelper;
    private int issueId;
    private Issue currentIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Issue Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvDetailIssueId = findViewById(R.id.tvDetailIssueId);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPriority = findViewById(R.id.tvDetailPriority);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailReporter = findViewById(R.id.tvDetailReporter);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvNoUpdates = findViewById(R.id.tvNoUpdates);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        layoutReporter = findViewById(R.id.layoutReporter);
        rvIssueUpdates = findViewById(R.id.rvIssueUpdates);
        btnEditIssue = findViewById(R.id.btnEditIssue);
        btnDeleteIssue = findViewById(R.id.btnDeleteIssue);

        databaseHelper = new DatabaseHelper(this);
        issueId = getIntent().getIntExtra("issue_id", -1);

        rvIssueUpdates.setLayoutManager(new LinearLayoutManager(this));

        btnEditIssue.setOnClickListener(v -> {
            Intent intent = new Intent(IssueDetailsActivity.this, EditIssueActivity.class);
            intent.putExtra("issue_id", issueId);
            startActivity(intent);
        });

        btnDeleteIssue.setOnClickListener(v -> confirmDelete());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIssueDetails();
    }

    private void loadIssueDetails() {
        if (issueId == -1) {
            Toast.makeText(this, "Issue not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentIssue = databaseHelper.getIssueById(issueId);
        if (currentIssue == null) {
            Toast.makeText(this, "Issue not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailIssueId.setText("Issue #" + currentIssue.getIssueId());
        tvDetailTitle.setText(currentIssue.getTitle());
        tvDetailCategory.setText(currentIssue.getCategory());

        String dateAndTime = currentIssue.getDate();
        if (currentIssue.getTime() != null && !currentIssue.getTime().isEmpty()) {
            dateAndTime += " " + currentIssue.getTime();
        }
        tvDetailDate.setText(dateAndTime);
        tvDetailDescription.setText(currentIssue.getDescription());

        // Image attachment preview
        if (currentIssue.getImageUri() != null && !currentIssue.getImageUri().isEmpty()) {
            ivDetailImage.setVisibility(View.VISIBLE);
        } else {
            ivDetailImage.setVisibility(View.GONE);
        }

        // Reporter with Department
        if (currentIssue.getUserName() != null && !currentIssue.getUserName().isEmpty()) {
            layoutReporter.setVisibility(View.VISIBLE);
            String reporterInfo = currentIssue.getUserName();
            if (currentIssue.getUserDepartment() != null && !currentIssue.getUserDepartment().isEmpty()) {
                reporterInfo += " (" + currentIssue.getUserDepartment() + ")";
            }
            tvDetailReporter.setText(reporterInfo);
        } else {
            layoutReporter.setVisibility(View.GONE);
        }

        // Status styling
        String status = currentIssue.getStatus();
        tvDetailStatus.setText(status);
        if ("Resolved".equalsIgnoreCase(status)) {
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_resolved);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_resolved));
            btnEditIssue.setVisibility(View.GONE); // Cannot edit resolved issue
        } else if ("In Progress".equalsIgnoreCase(status)) {
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_in_progress);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_in_progress));
            btnEditIssue.setVisibility(View.GONE);
        } else {
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_pending);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_pending));
            btnEditIssue.setVisibility(View.VISIBLE);
        }

        // Priority styling
        String priority = currentIssue.getPriority();
        tvDetailPriority.setText(priority + " Priority");
        if ("High".equalsIgnoreCase(priority)) {
            tvDetailPriority.setBackgroundResource(R.drawable.bg_badge_high);
            tvDetailPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_high));
        } else if ("Medium".equalsIgnoreCase(priority)) {
            tvDetailPriority.setBackgroundResource(R.drawable.bg_badge_medium);
            tvDetailPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_medium));
        } else {
            tvDetailPriority.setBackgroundResource(R.drawable.bg_badge_low);
            tvDetailPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_low));
        }

        // Updates Timeline
        List<IssueUpdate> updates = databaseHelper.getIssueUpdates(issueId);
        if (updates.isEmpty()) {
            tvNoUpdates.setVisibility(View.VISIBLE);
            rvIssueUpdates.setVisibility(View.GONE);
        } else {
            tvNoUpdates.setVisibility(View.GONE);
            rvIssueUpdates.setVisibility(View.VISIBLE);
            IssueUpdateAdapter adapter = new IssueUpdateAdapter(this, updates);
            rvIssueUpdates.setAdapter(adapter);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Issue")
                .setMessage("Are you sure you want to delete this issue? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = databaseHelper.deleteIssue(issueId);
                    if (success) {
                        Toast.makeText(this, "Issue deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete issue", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_my_issues) {
            finish();
            return true;
        } else if (id == R.id.menu_refresh) {
            loadIssueDetails();
            Toast.makeText(this, "Details refreshed", Toast.LENGTH_SHORT).show();
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
