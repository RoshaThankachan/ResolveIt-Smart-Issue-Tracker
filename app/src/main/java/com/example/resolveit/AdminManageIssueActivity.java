package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminManageIssueActivity extends AppCompatActivity {

    private TextView tvAdminManageIssueId, tvAdminManageTitle, tvAdminReporter;
    private TextView tvAdminCategory, tvAdminPriority, tvAdminDate, tvAdminDescription;
    private TextView tvAdminNoUpdates;
    private Spinner spinnerAdminStatus;
    private EditText etAdminUpdateMessage;
    private Button btnAdminSaveUpdate, btnAdminDeleteIssue;
    private RecyclerView rvAdminIssueUpdates;

    private DatabaseHelper databaseHelper;
    private int issueId;
    private Issue currentIssue;
    private String[] statuses = {"Pending", "In Progress", "Resolved"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_issue);

        tvAdminManageIssueId = findViewById(R.id.tvAdminManageIssueId);
        tvAdminManageTitle = findViewById(R.id.tvAdminManageTitle);
        tvAdminReporter = findViewById(R.id.tvAdminReporter);
        tvAdminCategory = findViewById(R.id.tvAdminCategory);
        tvAdminPriority = findViewById(R.id.tvAdminPriority);
        tvAdminDate = findViewById(R.id.tvAdminDate);
        tvAdminDescription = findViewById(R.id.tvAdminDescription);
        tvAdminNoUpdates = findViewById(R.id.tvAdminNoUpdates);

        spinnerAdminStatus = findViewById(R.id.spinnerAdminStatus);
        etAdminUpdateMessage = findViewById(R.id.etAdminUpdateMessage);
        btnAdminSaveUpdate = findViewById(R.id.btnAdminSaveUpdate);
        btnAdminDeleteIssue = findViewById(R.id.btnAdminDeleteIssue);
        rvAdminIssueUpdates = findViewById(R.id.rvAdminIssueUpdates);

        databaseHelper = new DatabaseHelper(this);
        issueId = getIntent().getIntExtra("issue_id", -1);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdminStatus.setAdapter(statusAdapter);

        rvAdminIssueUpdates.setLayoutManager(new LinearLayoutManager(this));

        btnAdminSaveUpdate.setOnClickListener(v -> saveAdminUpdate());
        btnAdminDeleteIssue.setOnClickListener(v -> confirmDelete());

        loadIssueData();
    }

    private void loadIssueData() {
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

        tvAdminManageIssueId.setText("Admin Management • Issue #" + currentIssue.getIssueId());
        tvAdminManageTitle.setText(currentIssue.getTitle());
        tvAdminCategory.setText(currentIssue.getCategory());
        tvAdminPriority.setText(currentIssue.getPriority() + " Priority");

        String dateTimeStr = currentIssue.getDate();
        if (currentIssue.getTime() != null && !currentIssue.getTime().isEmpty()) {
            dateTimeStr += " " + currentIssue.getTime();
        }
        tvAdminDate.setText(dateTimeStr);
        tvAdminDescription.setText(currentIssue.getDescription());

        // Fetch detailed User profile for reporter
        User reporter = databaseHelper.getUserById(currentIssue.getUserId());
        if (reporter != null) {
            String reporterDetails = reporter.getName() + "\nEmail: " + reporter.getEmail() +
                    "\nPhone: " + reporter.getPhone() + "\nDept: " + reporter.getDepartment();
            tvAdminReporter.setText(reporterDetails);
        } else if (currentIssue.getUserName() != null && !currentIssue.getUserName().isEmpty()) {
            tvAdminReporter.setText(currentIssue.getUserName());
        } else {
            tvAdminReporter.setText("User #" + currentIssue.getUserId());
        }

        // Set spinner selection based on current status
        String status = currentIssue.getStatus();
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(status)) {
                spinnerAdminStatus.setSelection(i);
                break;
            }
        }

        loadUpdatesList();
    }

    private void loadUpdatesList() {
        List<IssueUpdate> updates = databaseHelper.getIssueUpdates(issueId);
        if (updates.isEmpty()) {
            tvAdminNoUpdates.setVisibility(View.VISIBLE);
            rvAdminIssueUpdates.setVisibility(View.GONE);
        } else {
            tvAdminNoUpdates.setVisibility(View.GONE);
            rvAdminIssueUpdates.setVisibility(View.VISIBLE);
            IssueUpdateAdapter adapter = new IssueUpdateAdapter(this, updates);
            rvAdminIssueUpdates.setAdapter(adapter);
        }
    }

    private void saveAdminUpdate() {
        String newStatus = spinnerAdminStatus.getSelectedItem().toString();
        String updateMessage = etAdminUpdateMessage.getText().toString().trim();

        if (updateMessage.isEmpty()) {
            updateMessage = "Status updated to " + newStatus + " by Administrator.";
        }

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        boolean success = databaseHelper.updateIssueStatus(issueId, newStatus, updateMessage, currentDate, currentTime);
        if (success) {
            Toast.makeText(this, "Issue updated successfully!", Toast.LENGTH_SHORT).show();

            // Broadcast status update
            Intent broadcastIntent = new Intent(IssueNotificationReceiver.ACTION_ISSUE_STATUS_CHANGED);
            broadcastIntent.putExtra(IssueNotificationReceiver.EXTRA_MESSAGE, "Notification: Issue #" + issueId + " status updated to " + newStatus);
            sendBroadcast(broadcastIntent);

            etAdminUpdateMessage.setText("");
            loadIssueData();
        } else {
            Toast.makeText(this, "Failed to update issue", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Issue")
                .setMessage("Are you sure you want to delete Issue #" + issueId + "? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = databaseHelper.deleteIssue(issueId);
                    if (success) {
                        Toast.makeText(this, "Issue deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete issue", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
