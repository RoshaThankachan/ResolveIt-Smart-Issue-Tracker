package com.example.resolveit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IssueUpdateAdapter extends RecyclerView.Adapter<IssueUpdateAdapter.UpdateViewHolder> {

    private final Context context;
    private List<IssueUpdate> updateList;
    private boolean showIssueTitle = false;

    public IssueUpdateAdapter(Context context, List<IssueUpdate> updateList) {
        this.context = context;
        this.updateList = updateList;
    }

    public void setShowIssueTitle(boolean showIssueTitle) {
        this.showIssueTitle = showIssueTitle;
    }

    public void updateList(List<IssueUpdate> newList) {
        this.updateList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_update, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateViewHolder holder, int position) {
        IssueUpdate update = updateList.get(position);

        holder.tvUpdateMessage.setText(update.getUpdateMessage());
        holder.tvUpdateDate.setText(update.getUpdateDate());

        if (showIssueTitle && update.getIssueTitle() != null && !update.getIssueTitle().isEmpty()) {
            holder.tvUpdateIssueTitle.setVisibility(View.VISIBLE);
            holder.tvUpdateIssueTitle.setText("Issue: " + update.getIssueTitle());
        } else {
            holder.tvUpdateIssueTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return updateList != null ? updateList.size() : 0;
    }

    static class UpdateViewHolder extends RecyclerView.ViewHolder {
        TextView tvUpdateIssueTitle, tvUpdateMessage, tvUpdateDate;

        public UpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUpdateIssueTitle = itemView.findViewById(R.id.tvUpdateIssueTitle);
            tvUpdateMessage = itemView.findViewById(R.id.tvUpdateMessage);
            tvUpdateDate = itemView.findViewById(R.id.tvUpdateDate);
        }
    }
}
