package com.example.resolveit;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserUpdatesActivity extends AppCompatActivity {

    private RecyclerView rvUserUpdates;
    private LinearLayout layoutEmptyUpdates;
    private TextView tvUpdatesSubHeader;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_updates);

        rvUserUpdates = findViewById(R.id.rvUserUpdates);
        layoutEmptyUpdates = findViewById(R.id.layoutEmptyUpdates);
        tvUpdatesSubHeader = findViewById(R.id.tvUpdatesSubHeader);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);

        rvUserUpdates.setLayoutManager(new LinearLayoutManager(this));
        loadUpdates();
    }

    private void loadUpdates() {
        if (userId != -1) {
            List<IssueUpdate> updates = databaseHelper.getUserUpdates(userId);
            if (updates.isEmpty()) {
                layoutEmptyUpdates.setVisibility(View.VISIBLE);
                rvUserUpdates.setVisibility(View.GONE);
                tvUpdatesSubHeader.setText("0 updates found");
            } else {
                layoutEmptyUpdates.setVisibility(View.GONE);
                rvUserUpdates.setVisibility(View.VISIBLE);
                IssueUpdateAdapter adapter = new IssueUpdateAdapter(this, updates);
                adapter.setShowIssueTitle(true);
                rvUserUpdates.setAdapter(adapter);
                tvUpdatesSubHeader.setText(updates.size() + " update(s) recorded");
            }
        }
    }
}
