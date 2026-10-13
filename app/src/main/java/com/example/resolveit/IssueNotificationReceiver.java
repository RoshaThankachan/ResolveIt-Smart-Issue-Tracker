package com.example.resolveit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class IssueNotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_ISSUE_SUBMITTED = "com.example.resolveit.ACTION_ISSUE_SUBMITTED";
    public static final String ACTION_ISSUE_STATUS_CHANGED = "com.example.resolveit.ACTION_ISSUE_STATUS_CHANGED";
    public static final String EXTRA_MESSAGE = "extra_message";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String message = intent.getStringExtra(EXTRA_MESSAGE);

            if (message == null || message.isEmpty()) {
                if (ACTION_ISSUE_SUBMITTED.equals(action)) {
                    message = "Notification: New issue submitted successfully!";
                } else if (ACTION_ISSUE_STATUS_CHANGED.equals(action)) {
                    message = "Notification: Issue status updated!";
                } else {
                    message = "Notification received from ResolveIt System.";
                }
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
