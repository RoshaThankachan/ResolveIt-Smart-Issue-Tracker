package com.example.resolveit;

import java.io.Serializable;

public class IssueUpdate implements Serializable {
    private int updateId;
    private int issueId;
    private String issueTitle;
    private String status;
    private String remarks;
    private String updateDate;
    private String updateTime;

    public IssueUpdate(int updateId, int issueId, String issueTitle, String status, String remarks, String updateDate, String updateTime) {
        this.updateId = updateId;
        this.issueId = issueId;
        this.issueTitle = issueTitle;
        this.status = status;
        this.remarks = remarks;
        this.updateDate = updateDate;
        this.updateTime = updateTime;
    }

    public IssueUpdate(int updateId, int issueId, String issueTitle, String updateMessage, String updateDate) {
        this(updateId, issueId, issueTitle, "", updateMessage, updateDate, "");
    }

    public IssueUpdate(int updateId, int issueId, String updateMessage, String updateDate) {
        this(updateId, issueId, "", "", updateMessage, updateDate, "");
    }

    public int getUpdateId() {
        return updateId;
    }

    public int getIssueId() {
        return issueId;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public String getStatus() {
        return status != null ? status : "";
    }

    public String getRemarks() {
        return remarks != null ? remarks : "";
    }

    public String getUpdateMessage() {
        if (remarks != null && !remarks.isEmpty()) {
            return remarks;
        }
        return "Status updated to " + getStatus();
    }

    public String getUpdateDate() {
        return updateDate != null ? updateDate : "";
    }

    public String getUpdateTime() {
        return updateTime != null ? updateTime : "";
    }
}
