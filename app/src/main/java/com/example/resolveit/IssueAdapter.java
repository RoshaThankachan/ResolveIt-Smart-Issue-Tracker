package com.example.resolveit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    public interface OnIssueClickListener {
        void onIssueClick(Issue issue);
    }

    public interface OnIssueLongClickListener {
        boolean onIssueLongClick(Issue issue, View view);
    }

    private final Context context;
    private List<Issue> issueList;
    private final OnIssueClickListener listener;
    private OnIssueLongClickListener longClickListener;
    private boolean showReporter = false;

    public IssueAdapter(Context context, List<Issue> issueList, OnIssueClickListener listener) {
        this.context = context;
        this.issueList = issueList;
        this.listener = listener;
    }

    public IssueAdapter(Context context, List<Issue> issueList, OnIssueClickListener listener, OnIssueLongClickListener longClickListener) {
        this.context = context;
        this.issueList = issueList;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }

    public void setOnIssueLongClickListener(OnIssueLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void setShowReporter(boolean showReporter) {
        this.showReporter = showReporter;
    }

    public void updateList(List<Issue> newList) {
        this.issueList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue issue = issueList.get(position);

        holder.tvIssueTitle.setText(issue.getTitle());
        holder.tvIssueCategory.setText("Category: " + issue.getCategory());
        holder.tvIssueDescription.setText(issue.getDescription());
        holder.tvIssueDate.setText(issue.getDate() + (issue.getTime() != null && !issue.getTime().isEmpty() ? " " + issue.getTime() : ""));

        // Status Badge styling
        String status = issue.getStatus();
        holder.tvIssueStatus.setText(status);
        if ("Resolved".equalsIgnoreCase(status)) {
            holder.tvIssueStatus.setBackgroundResource(R.drawable.bg_badge_resolved);
            holder.tvIssueStatus.setTextColor(ContextCompat.getColor(context, R.color.status_resolved));
        } else if ("In Progress".equalsIgnoreCase(status)) {
            holder.tvIssueStatus.setBackgroundResource(R.drawable.bg_badge_in_progress);
            holder.tvIssueStatus.setTextColor(ContextCompat.getColor(context, R.color.status_in_progress));
        } else {
            holder.tvIssueStatus.setBackgroundResource(R.drawable.bg_badge_pending);
            holder.tvIssueStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
        }

        // Priority Badge styling
        String priority = issue.getPriority();
        holder.tvIssuePriority.setText(priority + " Priority");
        if ("High".equalsIgnoreCase(priority)) {
            holder.tvIssuePriority.setBackgroundResource(R.drawable.bg_badge_high);
            holder.tvIssuePriority.setTextColor(ContextCompat.getColor(context, R.color.priority_high));
        } else if ("Medium".equalsIgnoreCase(priority)) {
            holder.tvIssuePriority.setBackgroundResource(R.drawable.bg_badge_medium);
            holder.tvIssuePriority.setTextColor(ContextCompat.getColor(context, R.color.priority_medium));
        } else {
            holder.tvIssuePriority.setBackgroundResource(R.drawable.bg_badge_low);
            holder.tvIssuePriority.setTextColor(ContextCompat.getColor(context, R.color.priority_low));
        }

        // Reporter name (for admin views)
        if (showReporter && issue.getUserName() != null && !issue.getUserName().isEmpty()) {
            holder.tvIssueUser.setVisibility(View.VISIBLE);
            holder.tvIssueUser.setText("By: " + issue.getUserName() + (issue.getUserDepartment() != null && !issue.getUserDepartment().isEmpty() ? " (" + issue.getUserDepartment() + ")" : ""));
        } else {
            holder.tvIssueUser.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIssueClick(issue);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                return longClickListener.onIssueLongClick(issue, v);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return issueList != null ? issueList.size() : 0;
    }

    static class IssueViewHolder extends RecyclerView.ViewHolder {
        TextView tvIssueCategory, tvIssueStatus, tvIssueTitle, tvIssueDescription,
                tvIssuePriority, tvIssueUser, tvIssueDate;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIssueCategory = itemView.findViewById(R.id.tvIssueCategory);
            tvIssueStatus = itemView.findViewById(R.id.tvIssueStatus);
            tvIssueTitle = itemView.findViewById(R.id.tvIssueTitle);
            tvIssueDescription = itemView.findViewById(R.id.tvIssueDescription);
            tvIssuePriority = itemView.findViewById(R.id.tvIssuePriority);
            tvIssueUser = itemView.findViewById(R.id.tvIssueUser);
            tvIssueDate = itemView.findViewById(R.id.tvIssueDate);
        }
    }
}
