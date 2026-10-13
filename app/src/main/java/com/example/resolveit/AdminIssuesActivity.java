package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminIssuesActivity extends AppCompatActivity {

    private RecyclerView rvAdminIssues;
    private LinearLayout layoutAdminEmpty;
    private TextView tvAdminIssuesCount;
    private Spinner spinnerFilterStatus, spinnerFilterCategory, spinnerFilterPriority;

    private DatabaseHelper databaseHelper;
    private IssueAdapter issueAdapter;
    private List<Issue> issueList = new ArrayList<>();

    private String selectedStatus = "All";
    private String selectedCategory = "All";
    private String selectedPriority = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_issues);

        rvAdminIssues = findViewById(R.id.rvAdminIssues);
        layoutAdminEmpty = findViewById(R.id.layoutAdminEmpty);
        tvAdminIssuesCount = findViewById(R.id.tvAdminIssuesCount);
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus);
        spinnerFilterCategory = findViewById(R.id.spinnerFilterCategory);
        spinnerFilterPriority = findViewById(R.id.spinnerFilterPriority);

        databaseHelper = new DatabaseHelper(this);

        String initialStatus = getIntent().getStringExtra("initial_status");
        if (initialStatus != null && !initialStatus.isEmpty()) {
            selectedStatus = initialStatus;
        }

        setupFilters();

        rvAdminIssues.setLayoutManager(new LinearLayoutManager(this));
        issueAdapter = new IssueAdapter(this, issueList, issue -> {
            Intent intent = new Intent(AdminIssuesActivity.this, AdminManageIssueActivity.class);
            intent.putExtra("issue_id", issue.getIssueId());
            startActivity(intent);
        });
        issueAdapter.setShowReporter(true);
        rvAdminIssues.setAdapter(issueAdapter);
    }

    private void setupFilters() {
        // Status Filter
        String[] statuses = {"All Status", "Pending", "In Progress", "Resolved"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterStatus.setAdapter(statusAdapter);

        if ("Pending".equalsIgnoreCase(selectedStatus)) {
            spinnerFilterStatus.setSelection(1);
        } else if ("In Progress".equalsIgnoreCase(selectedStatus)) {
            spinnerFilterStatus.setSelection(2);
        } else if ("Resolved".equalsIgnoreCase(selectedStatus)) {
            spinnerFilterStatus.setSelection(3);
        } else {
            spinnerFilterStatus.setSelection(0);
        }

        // Category Filter
        String[] categories = {"All Categories", "Technical", "Electrical", "Maintenance", "Cleanliness", "Internet/Wi-Fi", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterCategory.setAdapter(categoryAdapter);

        // Priority Filter
        String[] priorities = {"All Priority", "High", "Medium", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterPriority.setAdapter(priorityAdapter);

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int statusPos = spinnerFilterStatus.getSelectedItemPosition();
                selectedStatus = statusPos == 0 ? "All" : statuses[statusPos];

                int catPos = spinnerFilterCategory.getSelectedItemPosition();
                selectedCategory = catPos == 0 ? "All" : categories[catPos];

                int priPos = spinnerFilterPriority.getSelectedItemPosition();
                selectedPriority = priPos == 0 ? "All" : priorities[priPos];

                loadIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spinnerFilterStatus.setOnItemSelectedListener(filterListener);
        spinnerFilterCategory.setOnItemSelectedListener(filterListener);
        spinnerFilterPriority.setOnItemSelectedListener(filterListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIssues();
    }

    private void loadIssues() {
        issueList = databaseHelper.getAllIssues(selectedStatus, selectedCategory, selectedPriority);
        issueAdapter.updateList(issueList);

        if (issueList.isEmpty()) {
            layoutAdminEmpty.setVisibility(View.VISIBLE);
            rvAdminIssues.setVisibility(View.GONE);
        } else {
            layoutAdminEmpty.setVisibility(View.GONE);
            rvAdminIssues.setVisibility(View.VISIBLE);
        }

        tvAdminIssuesCount.setText("Showing " + issueList.size() + " issue(s)");
    }
}
