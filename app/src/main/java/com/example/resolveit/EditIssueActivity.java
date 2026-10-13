package com.example.resolveit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class EditIssueActivity extends AppCompatActivity {

    private EditText etEditTitle, etEditDescription;
    private Spinner spinnerEditCategory;
    private RadioGroup rgEditPriority;
    private RadioButton rbEditLow, rbEditMedium, rbEditHigh;
    private Button btnEditSelectDate, btnEditSelectTime, btnSaveIssueChanges;
    private TextView tvEditSelectedDate, tvEditSelectedTime, tvEditIssueHeader;

    private DatabaseHelper databaseHelper;
    private int issueId;
    private Issue currentIssue;
    private String selectedDate;
    private String selectedTime;
    private String[] categories = {"Technical", "Electrical", "Maintenance", "Cleanliness", "Internet/Wi-Fi", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_issue);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Issue");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvEditIssueHeader = findViewById(R.id.tvEditIssueHeader);
        etEditTitle = findViewById(R.id.etEditTitle);
        etEditDescription = findViewById(R.id.etEditDescription);
        spinnerEditCategory = findViewById(R.id.spinnerEditCategory);

        rgEditPriority = findViewById(R.id.rgEditPriority);
        rbEditLow = findViewById(R.id.rbEditLow);
        rbEditMedium = findViewById(R.id.rbEditMedium);
        rbEditHigh = findViewById(R.id.rbEditHigh);

        btnEditSelectDate = findViewById(R.id.btnEditSelectDate);
        btnEditSelectTime = findViewById(R.id.btnEditSelectTime);
        btnSaveIssueChanges = findViewById(R.id.btnSaveIssueChanges);

        tvEditSelectedDate = findViewById(R.id.tvEditSelectedDate);
        tvEditSelectedTime = findViewById(R.id.tvEditSelectedTime);

        databaseHelper = new DatabaseHelper(this);
        issueId = getIntent().getIntExtra("issue_id", -1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditCategory.setAdapter(adapter);

        btnEditSelectDate.setOnClickListener(v -> showDatePicker());
        btnEditSelectTime.setOnClickListener(v -> showTimePicker());
        btnSaveIssueChanges.setOnClickListener(v -> saveChanges());

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

        tvEditIssueHeader.setText("Edit Issue #" + currentIssue.getIssueId());
        etEditTitle.setText(currentIssue.getTitle());
        etEditDescription.setText(currentIssue.getDescription());

        // Category spinner selection
        String category = currentIssue.getCategory();
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(category)) {
                spinnerEditCategory.setSelection(i);
                break;
            }
        }

        // Priority radio button selection
        String priority = currentIssue.getPriority();
        if ("High".equalsIgnoreCase(priority)) {
            rbEditHigh.setChecked(true);
        } else if ("Low".equalsIgnoreCase(priority)) {
            rbEditLow.setChecked(true);
        } else {
            rbEditMedium.setChecked(true);
        }

        selectedDate = currentIssue.getDate();
        selectedTime = currentIssue.getTime();

        tvEditSelectedDate.setText(selectedDate != null ? selectedDate : "Select Date");
        tvEditSelectedTime.setText(selectedTime != null ? selectedTime : "Select Time");
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    tvEditSelectedDate.setText(selectedDate);
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hourIn12 = hourOfDay % 12;
                    if (hourIn12 == 0) hourIn12 = 12;
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12, minute, amPm);
                    tvEditSelectedTime.setText(selectedTime);
                },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false
        );
        timePickerDialog.show();
    }

    private void saveChanges() {
        String title = etEditTitle.getText().toString().trim();
        String description = etEditDescription.getText().toString().trim();
        String category = spinnerEditCategory.getSelectedItem().toString();

        String priority = "Medium";
        int checkedId = rgEditPriority.getCheckedRadioButtonId();
        if (checkedId == R.id.rbEditLow) {
            priority = "Low";
        } else if (checkedId == R.id.rbEditHigh) {
            priority = "High";
        }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.updateIssue(issueId, title, category, description, priority, selectedDate, selectedTime, currentIssue.getImageUri());

        if (success) {
            Toast.makeText(this, "Issue updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update issue", Toast.LENGTH_SHORT).show();
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
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_my_issues) {
            finish();
            return true;
        } else if (id == R.id.menu_refresh) {
            loadIssueData();
            Toast.makeText(this, "Issue data reloaded", Toast.LENGTH_SHORT).show();
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
