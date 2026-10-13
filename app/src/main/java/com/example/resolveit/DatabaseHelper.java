package com.example.resolveit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ResolveIt.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table 1: Users
        db.execSQL("CREATE TABLE Users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "phone TEXT, " +
                "department TEXT, " +
                "password TEXT NOT NULL, " +
                "role TEXT DEFAULT 'USER')");

        // Table 2: Issues
        db.execSQL("CREATE TABLE Issues (" +
                "issue_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "title TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "description TEXT, " +
                "priority TEXT NOT NULL, " +
                "status TEXT DEFAULT 'Pending', " +
                "date TEXT NOT NULL, " +
                "time TEXT, " +
                "image_uri TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE)");

        // Table 3: Issue_Updates
        db.execSQL("CREATE TABLE Issue_Updates (" +
                "update_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "issue_id INTEGER, " +
                "status TEXT, " +
                "remarks TEXT, " +
                "update_message TEXT, " +
                "update_date TEXT NOT NULL, " +
                "update_time TEXT, " +
                "FOREIGN KEY(issue_id) REFERENCES Issues(issue_id) ON DELETE CASCADE)");

        // Seed default Admin user
        ContentValues adminValues = new ContentValues();
        adminValues.put("name", "System Administrator");
        adminValues.put("email", "admin@resolveit.com");
        adminValues.put("phone", "9876543210");
        adminValues.put("department", "IT Administration");
        adminValues.put("password", "admin123");
        adminValues.put("role", "ADMIN");
        db.insert("Users", null, adminValues);

        // Seed sample Demo user
        ContentValues demoUser = new ContentValues();
        demoUser.put("name", "Alex Johnson");
        demoUser.put("email", "alex@example.com");
        demoUser.put("phone", "9876500001");
        demoUser.put("department", "Computer Science");
        demoUser.put("password", "user123");
        demoUser.put("role", "USER");
        long userId = db.insert("Users", null, demoUser);

        // Seed initial sample issues for demo
        if (userId != -1) {
            seedSampleIssues(db, (int) userId);
        }
    }

    private void seedSampleIssues(SQLiteDatabase db, int userId) {
        // Sample Issue 1
        ContentValues issue1 = new ContentValues();
        issue1.put("user_id", userId);
        issue1.put("title", "Wi-Fi not working in Lab 2");
        issue1.put("category", "Internet/Wi-Fi");
        issue1.put("description", "Internet connection is down across all workstations in Computer Lab 2.");
        issue1.put("priority", "High");
        issue1.put("status", "In Progress");
        issue1.put("date", "14/09/2026");
        issue1.put("time", "10:00 AM");
        issue1.put("image_uri", "");
        long id1 = db.insert("Issues", null, issue1);

        if (id1 != -1) {
            ContentValues update1 = new ContentValues();
            update1.put("issue_id", id1);
            update1.put("status", "Pending");
            update1.put("remarks", "Issue reported and pending review.");
            update1.put("update_message", "Issue reported and pending review.");
            update1.put("update_date", "14/09/2026");
            update1.put("update_time", "10:00 AM");
            db.insert("Issue_Updates", null, update1);

            ContentValues update2 = new ContentValues();
            update2.put("issue_id", id1);
            update2.put("status", "In Progress");
            update2.put("remarks", "Network technician assigned to inspect router & switch.");
            update2.put("update_message", "Network technician assigned to inspect router & switch.");
            update2.put("update_date", "14/09/2026");
            update2.put("update_time", "11:30 AM");
            db.insert("Issue_Updates", null, update2);
        }

        // Sample Issue 2
        ContentValues issue2 = new ContentValues();
        issue2.put("user_id", userId);
        issue2.put("title", "Projector bulb flickering");
        issue2.put("category", "Electrical");
        issue2.put("description", "Projector in Seminar Hall 101 flickers intermittently during presentations.");
        issue2.put("priority", "Medium");
        issue2.put("status", "Pending");
        issue2.put("date", "15/09/2026");
        issue2.put("time", "09:15 AM");
        issue2.put("image_uri", "");
        long id2 = db.insert("Issues", null, issue2);

        if (id2 != -1) {
            ContentValues update = new ContentValues();
            update.put("issue_id", id2);
            update.put("status", "Pending");
            update.put("remarks", "Issue submitted and awaiting admin assessment.");
            update.put("update_message", "Issue submitted and awaiting admin assessment.");
            update.put("update_date", "15/09/2026");
            update.put("update_time", "09:15 AM");
            db.insert("Issue_Updates", null, update);
        }

        // Sample Issue 3
        ContentValues issue3 = new ContentValues();
        issue3.put("user_id", userId);
        issue3.put("title", "AC Water Leakage");
        issue3.put("category", "Maintenance");
        issue3.put("description", "Water dripping from the air conditioner unit on 3rd floor corridor.");
        issue3.put("priority", "Low");
        issue3.put("status", "Resolved");
        issue3.put("date", "12/09/2026");
        issue3.put("time", "08:30 AM");
        issue3.put("image_uri", "");
        long id3 = db.insert("Issues", null, issue3);

        if (id3 != -1) {
            ContentValues update1 = new ContentValues();
            update1.put("issue_id", id3);
            update1.put("status", "Pending");
            update1.put("remarks", "Issue reported.");
            update1.put("update_message", "Issue reported.");
            update1.put("update_date", "12/09/2026");
            update1.put("update_time", "08:30 AM");
            db.insert("Issue_Updates", null, update1);

            ContentValues update2 = new ContentValues();
            update2.put("issue_id", id3);
            update2.put("status", "Resolved");
            update2.put("remarks", "Drain pipe cleaned and sealed. Issue resolved.");
            update2.put("update_message", "Drain pipe cleaned and sealed. Issue resolved.");
            update2.put("update_date", "12/09/2026");
            update2.put("update_time", "03:00 PM");
            db.insert("Issue_Updates", null, update2);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Issue_Updates");
        db.execSQL("DROP TABLE IF EXISTS Issues");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    // --- User Operations ---

    public long registerUser(String name, String email, String phone, String password) {
        return registerUser(name, email, phone, "General", password);
    }

    public long registerUser(String name, String email, String phone, String department, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("department", department != null ? department : "General");
        values.put("password", password);
        values.put("role", "USER");
        return db.insert("Users", null, values);
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Users WHERE email = ? AND password = ?",
                new String[]{email, password}
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
            cursor.close();
        }
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Users WHERE email = ?",
                new String[]{email}
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
            cursor.close();
        }
        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Users WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = extractUserFromCursor(cursor);
            cursor.close();
        }
        return user;
    }

    public boolean updateUserProfile(int userId, String name, String phone, String department) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        values.put("department", department);
        int rows = db.update("Users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    private User extractUserFromCursor(Cursor cursor) {
        String dept = "";
        try {
            int deptIndex = cursor.getColumnIndex("department");
            if (deptIndex != -1) {
                dept = cursor.getString(deptIndex);
            }
        } catch (Exception ignored) {
        }
        return new User(
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                dept != null ? dept : "General",
                cursor.getString(cursor.getColumnIndexOrThrow("password")),
                cursor.getString(cursor.getColumnIndexOrThrow("role"))
        );
    }

    // --- Issue Operations ---

    public long insertIssue(int userId, String title, String category, String description,
                            String priority, String date) {
        return insertIssue(userId, title, category, description, priority, date, "10:00 AM", "");
    }

    public long insertIssue(int userId, String title, String category, String description,
                            String priority, String date, String time, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("category", category);
        values.put("description", description);
        values.put("priority", priority);
        values.put("status", "Pending");
        values.put("date", date);
        values.put("time", time != null ? time : "10:00 AM");
        values.put("image_uri", imageUri != null ? imageUri : "");

        long issueId = db.insert("Issues", null, values);

        if (issueId != -1) {
            ContentValues updateValues = new ContentValues();
            updateValues.put("issue_id", issueId);
            updateValues.put("status", "Pending");
            updateValues.put("remarks", "Issue reported with status Pending.");
            updateValues.put("update_message", "Issue reported with status Pending.");
            updateValues.put("update_date", date);
            updateValues.put("update_time", time != null ? time : "10:00 AM");
            db.insert("Issue_Updates", null, updateValues);
        }

        return issueId;
    }

    public boolean updateIssue(int issueId, String title, String category, String description,
                               String priority, String date, String time, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("category", category);
        values.put("description", description);
        values.put("priority", priority);
        values.put("date", date);
        values.put("time", time);
        if (imageUri != null) {
            values.put("image_uri", imageUri);
        }
        int rows = db.update("Issues", values, "issue_id = ?", new String[]{String.valueOf(issueId)});
        return rows > 0;
    }

    public List<Issue> getUserIssues(int userId, String statusFilter) {
        List<Issue> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT i.*, u.name as user_name, u.department as user_department FROM Issues i LEFT JOIN Users u ON i.user_id = u.user_id WHERE i.user_id = ?";
        List<String> argsList = new ArrayList<>();
        argsList.add(String.valueOf(userId));

        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase("All")) {
            query += " AND i.status = ?";
            argsList.add(statusFilter);
        }

        query += " ORDER BY i.issue_id DESC";

        Cursor cursor = db.rawQuery(query, argsList.toArray(new String[0]));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(extractIssueFromCursor(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public List<Issue> getAllIssues(String statusFilter, String categoryFilter, String priorityFilter) {
        List<Issue> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder(
                "SELECT i.*, u.name as user_name, u.department as user_department FROM Issues i LEFT JOIN Users u ON i.user_id = u.user_id WHERE 1=1"
        );
        List<String> args = new ArrayList<>();

        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase("All")) {
            query.append(" AND i.status = ?");
            args.add(statusFilter);
        }
        if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equalsIgnoreCase("All")) {
            query.append(" AND i.category = ?");
            args.add(categoryFilter);
        }
        if (priorityFilter != null && !priorityFilter.isEmpty() && !priorityFilter.equalsIgnoreCase("All")) {
            query.append(" AND i.priority = ?");
            args.add(priorityFilter);
        }

        query.append(" ORDER BY i.issue_id DESC");

        Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(extractIssueFromCursor(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public Issue getIssueById(int issueId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT i.*, u.name as user_name, u.department as user_department FROM Issues i LEFT JOIN Users u ON i.user_id = u.user_id WHERE i.issue_id = ?",
                new String[]{String.valueOf(issueId)}
        );

        Issue issue = null;
        if (cursor != null && cursor.moveToFirst()) {
            issue = extractIssueFromCursor(cursor);
            cursor.close();
        }
        return issue;
    }

    private Issue extractIssueFromCursor(Cursor cursor) {
        String timeStr = "10:00 AM";
        String imageUriStr = "";
        String deptStr = "";

        try {
            int timeIdx = cursor.getColumnIndex("time");
            if (timeIdx != -1) timeStr = cursor.getString(timeIdx);
        } catch (Exception ignored) {}

        try {
            int imgIdx = cursor.getColumnIndex("image_uri");
            if (imgIdx != -1) imageUriStr = cursor.getString(imgIdx);
        } catch (Exception ignored) {}

        try {
            int deptIdx = cursor.getColumnIndex("user_department");
            if (deptIdx != -1) deptStr = cursor.getString(deptIdx);
        } catch (Exception ignored) {}

        return new Issue(
                cursor.getInt(cursor.getColumnIndexOrThrow("issue_id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("user_name")),
                deptStr != null ? deptStr : "",
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("category")),
                cursor.getString(cursor.getColumnIndexOrThrow("description")),
                cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                timeStr != null ? timeStr : "10:00 AM",
                imageUriStr != null ? imageUriStr : ""
        );
    }

    public boolean updateIssueStatus(int issueId, String newStatus, String updateMessage, String updateDate) {
        return updateIssueStatus(issueId, newStatus, updateMessage, updateDate, "10:00 AM");
    }

    public boolean updateIssueStatus(int issueId, String newStatus, String remarks, String updateDate, String updateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        int rows = db.update("Issues", values, "issue_id = ?", new String[]{String.valueOf(issueId)});

        if (rows > 0) {
            if (remarks != null && !remarks.trim().isEmpty()) {
                ContentValues updateValues = new ContentValues();
                updateValues.put("issue_id", issueId);
                updateValues.put("status", newStatus);
                updateValues.put("remarks", remarks.trim());
                updateValues.put("update_message", remarks.trim());
                updateValues.put("update_date", updateDate);
                updateValues.put("update_time", updateTime);
                db.insert("Issue_Updates", null, updateValues);
            }
            return true;
        }
        return false;
    }

    public boolean addIssueUpdate(int issueId, String updateMessage, String updateDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put("issue_id", issueId);
        updateValues.put("status", "Updated");
        updateValues.put("remarks", updateMessage.trim());
        updateValues.put("update_message", updateMessage.trim());
        updateValues.put("update_date", updateDate);
        updateValues.put("update_time", "10:00 AM");
        long id = db.insert("Issue_Updates", null, updateValues);
        return id != -1;
    }

    public boolean deleteIssue(int issueId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Issue_Updates", "issue_id = ?", new String[]{String.valueOf(issueId)});
        int rows = db.delete("Issues", "issue_id = ?", new String[]{String.valueOf(issueId)});
        return rows > 0;
    }

    // --- Issue Updates Operations ---

    public List<IssueUpdate> getIssueUpdates(int issueId) {
        List<IssueUpdate> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Issue_Updates WHERE issue_id = ? ORDER BY update_id DESC",
                new String[]{String.valueOf(issueId)}
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(extractUpdateFromCursor(cursor, ""));
            }
            cursor.close();
        }
        return list;
    }

    public List<IssueUpdate> getUserUpdates(int userId) {
        List<IssueUpdate> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT u.*, i.title as issue_title FROM Issue_Updates u " +
                        "JOIN Issues i ON u.issue_id = i.issue_id " +
                        "WHERE i.user_id = ? ORDER BY u.update_id DESC",
                new String[]{String.valueOf(userId)}
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = "";
                try {
                    int titleIdx = cursor.getColumnIndex("issue_title");
                    if (titleIdx != -1) title = cursor.getString(titleIdx);
                } catch (Exception ignored) {}
                list.add(extractUpdateFromCursor(cursor, title));
            }
            cursor.close();
        }
        return list;
    }

    private IssueUpdate extractUpdateFromCursor(Cursor cursor, String fallbackTitle) {
        String status = "";
        String remarks = "";
        String updateTime = "";

        try {
            int stIdx = cursor.getColumnIndex("status");
            if (stIdx != -1) status = cursor.getString(stIdx);
        } catch (Exception ignored) {}

        try {
            int rmIdx = cursor.getColumnIndex("remarks");
            if (rmIdx != -1) remarks = cursor.getString(rmIdx);
        } catch (Exception ignored) {}

        try {
            int msgIdx = cursor.getColumnIndex("update_message");
            if ((remarks == null || remarks.isEmpty()) && msgIdx != -1) {
                remarks = cursor.getString(msgIdx);
            }
        } catch (Exception ignored) {}

        try {
            int timeIdx = cursor.getColumnIndex("update_time");
            if (timeIdx != -1) updateTime = cursor.getString(timeIdx);
        } catch (Exception ignored) {}

        return new IssueUpdate(
                cursor.getInt(cursor.getColumnIndexOrThrow("update_id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("issue_id")),
                fallbackTitle != null ? fallbackTitle : "",
                status != null ? status : "",
                remarks != null ? remarks : "",
                cursor.getString(cursor.getColumnIndexOrThrow("update_date")),
                updateTime != null ? updateTime : ""
        );
    }

    public int[] getIssueCounts(Integer userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int total = 0, pending = 0, inProgress = 0, resolved = 0;

        String query = "SELECT status, COUNT(*) as count FROM Issues";
        String[] args = null;
        if (userId != null) {
            query += " WHERE user_id = ?";
            args = new String[]{String.valueOf(userId)};
        }
        query += " GROUP BY status";

        Cursor cursor = db.rawQuery(query, args);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String status = cursor.getString(0);
                int count = cursor.getInt(1);
                total += count;
                if ("Pending".equalsIgnoreCase(status)) {
                    pending = count;
                } else if ("In Progress".equalsIgnoreCase(status)) {
                    inProgress = count;
                } else if ("Resolved".equalsIgnoreCase(status)) {
                    resolved = count;
                }
            }
            cursor.close();
        }

        return new int[]{total, pending, inProgress, resolved};
    }
}