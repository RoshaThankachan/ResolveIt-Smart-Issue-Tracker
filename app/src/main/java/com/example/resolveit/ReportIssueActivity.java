package com.example.resolveit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportIssueActivity extends AppCompatActivity {

    private EditText etIssueTitle, etDescription;
    private Spinner spinnerCategory;
    private RadioGroup rgPriority;
    private RadioButton rbLow, rbMedium, rbHigh;
    private Button btnSelectDate, btnSelectTime, btnAttachImage, btnSubmitIssue;
    private TextView tvSelectedDate, tvSelectedTime;
    private ImageView ivIssueImage;
    private CheckBox cbConfirmDetails;

    private DatabaseHelper databaseHelper;
    private int userId;
    private String selectedDate;
    private String selectedTime;
    private String attachedImageUri = "";
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Report Issue");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etIssueTitle = findViewById(R.id.etIssueTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        rgPriority = findViewById(R.id.rgPriority);
        rbLow = findViewById(R.id.rbLow);
        rbMedium = findViewById(R.id.rbMedium);
        rbHigh = findViewById(R.id.rbHigh);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnAttachImage = findViewById(R.id.btnAttachImage);
        btnSubmitIssue = findViewById(R.id.btnSubmitIssue);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        ivIssueImage = findViewById(R.id.ivIssueImage);
        cbConfirmDetails = findViewById(R.id.cbConfirmDetails);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("user_id", -1);
        String userEmail = getIntent().getStringExtra("user_email");

        if (userId == -1 && userEmail != null) {
            User user = databaseHelper.getUserByEmail(userEmail);
            if (user != null) userId = user.getUserId();
        }

        calendar = Calendar.getInstance();
        selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        selectedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        tvSelectedDate.setText(selectedDate);
        tvSelectedTime.setText(selectedTime);

        setupCategorySpinner();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnAttachImage.setOnClickListener(v -> attachSamplePhoto());
        btnSubmitIssue.setOnClickListener(v -> submitIssue());
    }

    private void setupCategorySpinner() {
        String[] categories = {
                "Technical",
                "Electrical",
                "Maintenance",
                "Cleanliness",
                "Internet/Wi-Fi",
                "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, yearSelected);
                    tvSelectedDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteSelected) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int hourIn12 = hourOfDay % 12;
                    if (hourIn12 == 0) hourIn12 = 12;
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12, minuteSelected, amPm);
                    tvSelectedTime.setText(selectedTime);
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }

    private void attachSamplePhoto() {
        attachedImageUri = "sample_attached_photo";
        ivIssueImage.setImageResource(R.drawable.ic_launcher_foreground);
        Toast.makeText(this, "Sample photo attached", Toast.LENGTH_SHORT).show();
    }

    private void submitIssue() {
        String title = etIssueTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        String priority = "Medium";
        int selectedRadioId = rgPriority.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.rbLow) {
            priority = "Low";
        } else if (selectedRadioId == R.id.rbHigh) {
            priority = "High";
        }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbConfirmDetails.isChecked()) {
            Toast.makeText(this, "Please check the confirmation box before submitting", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "Session error: user not identified", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = databaseHelper.insertIssue(userId, title, category, description, priority, selectedDate, selectedTime, attachedImageUri);

        if (result != -1) {
            Toast.makeText(this, "Issue submitted successfully!", Toast.LENGTH_SHORT).show();

            // Broadcast Intent Demonstration
            Intent broadcastIntent = new Intent(IssueNotificationReceiver.ACTION_ISSUE_SUBMITTED);
            broadcastIntent.putExtra(IssueNotificationReceiver.EXTRA_MESSAGE, "Notification: Issue #" + result + " reported successfully!");
            sendBroadcast(broadcastIntent);

            finish();
        } else {
            Toast.makeText(this, "Failed to submit issue. Please try again.", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(this, MyIssuesActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_refresh) {
            etIssueTitle.setText("");
            etDescription.setText("");
            cbConfirmDetails.setChecked(false);
            Toast.makeText(this, "Form reset", Toast.LENGTH_SHORT).show();
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
