package com.example.resolveit;

import java.io.Serializable;

public class Issue implements Serializable {

    private int issueId;
    private int userId;
    private String userName;
    private String userDepartment;
    private String title;
    private String category;
    private String description;
    private String priority;
    private String status;
    private String date;
    private String time;
    private String imageUri;

    public Issue(int issueId, int userId, String userName, String userDepartment, String title, String category,
                 String description, String priority, String status, String date, String time, String imageUri) {
        this.issueId = issueId;
        this.userId = userId;
        this.userName = userName;
        this.userDepartment = userDepartment;
        this.title = title;
        this.category = category;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.date = date;
        this.time = time;
        this.imageUri = imageUri;
    }

    public Issue(int issueId, int userId, String userName, String title, String category,
                 String description, String priority, String status, String date) {
        this(issueId, userId, userName, "", title, category, description, priority, status, date, "10:00 AM", "");
    }

    public Issue(int issueId, String title, String category,
                 String priority, String status, String date) {
        this(issueId, 0, "", "", title, category, "", priority, status, date, "10:00 AM", "");
    }

    public int getIssueId() {
        return issueId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDepartment() {
        return userDepartment != null ? userDepartment : "";
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time != null ? time : "";
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUri() {
        return imageUri != null ? imageUri : "";
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
