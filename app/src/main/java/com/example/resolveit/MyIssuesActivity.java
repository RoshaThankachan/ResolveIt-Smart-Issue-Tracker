package com.example.resolveit;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MyIssuesActivity extends AppCompatActivity {

    private RecyclerView rvMyIssues;
    private LinearLayout layoutEmpty;
    private TextView tvIssueSubHeader;
    private ChipGroup chipGroupStatus;
    private Chip chipAll, chipPending, chipInProgress, chipResolved;

    private DatabaseHelper databaseHelper;
    private IssueAdapter issueAdapter;
    private List<Issue> issueList = new ArrayList<>();
    private int userId;
    private String selectedStatus = "All";

    private Issue selectedContextIssue; // Issue target for context menu actions

    private static final int CONTEXT_MENU_VIEW = 101;
    private static final int CONTEXT_MENU_EDIT = 102;
    private static final int CONTEXT_MENU_DELETE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_issues);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Issues");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvMyIssues = findViewById(R.id.rvMyIssues);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvIssueSubHeader = findViewById(R.id.tvIssueSubHeader);
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipInProgress = findViewById(R.id.chipInProgress);
        chipResolved = findViewById(R.id.chipResolved);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);

        rvMyIssues.setLayoutManager(new LinearLayoutManager(this));

        issueAdapter = new IssueAdapter(
                this,
                issueList,
                issue -> {
                    Intent intent = new Intent(MyIssuesActivity.this, IssueDetailsActivity.class);
                    intent.putExtra("issue_id", issue.getIssueId());
                    startActivity(intent);
                },
                (issue, view) -> {
                    selectedContextIssue = issue;
                    registerForContextMenu(view);
                    openContextMenu(view);
                    unregisterForContextMenu(view);
                    return true;
                }
        );

        rvMyIssues.setAdapter(issueAdapter);

        // Chip selection listener
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedStatus = "All";
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipPending) {
                    selectedStatus = "Pending";
                } else if (checkedId == R.id.chipInProgress) {
                    selectedStatus = "In Progress";
                } else if (checkedId == R.id.chipResolved) {
                    selectedStatus = "Resolved";
                } else {
                    selectedStatus = "All";
                }
            }
            loadIssues();
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (selectedContextIssue != null) {
            menu.setHeaderTitle("Issue #" + selectedContextIssue.getIssueId() + ": " + selectedContextIssue.getTitle());
            menu.add(0, CONTEXT_MENU_VIEW, 0, "View Details");

            // Allow Edit only if status is Pending
            if ("Pending".equalsIgnoreCase(selectedContextIssue.getStatus())) {
                menu.add(0, CONTEXT_MENU_EDIT, 1, "Edit Issue");
            }
            menu.add(0, CONTEXT_MENU_DELETE, 2, "Delete Issue");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (selectedContextIssue == null) return super.onContextItemSelected(item);

        int itemId = item.getItemId();
        if (itemId == CONTEXT_MENU_VIEW) {
            Intent intent = new Intent(MyIssuesActivity.this, IssueDetailsActivity.class);
            intent.putExtra("issue_id", selectedContextIssue.getIssueId());
            startActivity(intent);
            return true;
        } else if (itemId == CONTEXT_MENU_EDIT) {
            Intent intent = new Intent(MyIssuesActivity.this, EditIssueActivity.class);
            intent.putExtra("issue_id", selectedContextIssue.getIssueId());
            startActivity(intent);
            return true;
        } else if (itemId == CONTEXT_MENU_DELETE) {
            confirmDeleteIssue(selectedContextIssue);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void confirmDeleteIssue(Issue issue) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Issue")
                .setMessage("Are you sure you want to delete Issue #" + issue.getIssueId() + "? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = databaseHelper.deleteIssue(issue.getIssueId());
                    if (deleted) {
                        Toast.makeText(this, "Issue deleted successfully", Toast.LENGTH_SHORT).show();
                        loadIssues();
                    } else {
                        Toast.makeText(this, "Failed to delete issue", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIssues();
    }

    private void loadIssues() {
        if (userId != -1) {
            issueList = databaseHelper.getUserIssues(userId, selectedStatus);
            issueAdapter.updateList(issueList);

            if (issueList.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                rvMyIssues.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
                rvMyIssues.setVisibility(View.VISIBLE);
            }

            tvIssueSubHeader.setText(issueList.size() + " issue(s) found");
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
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_my_issues) {
            Toast.makeText(this, "Already on My Issues", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_refresh) {
            loadIssues();
            Toast.makeText(this, "Issues refreshed", Toast.LENGTH_SHORT).show();
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
