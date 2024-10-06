package com.ermayurmahajan.mcoeadminapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignmentListRecyclerAdapter extends RecyclerView.Adapter<AssignmentListRecyclerAdapter.AssignmentViewHolder> {

    private List<AssignmentListModel> assignmentList;

    public AssignmentListRecyclerAdapter(List<AssignmentListModel> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_card_view, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentListModel assignment = assignmentList.get(position);
        holder.tvAssignmentTitle.setText(assignment.getTitle());
        holder.tvStartDate.setText("Start Date: " + assignment.getStartDate());
        holder.tvEndDate.setText("End Date: " + assignment.getEndDate());
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssignmentTitle;
        TextView tvStartDate;
        TextView tvEndDate;

        AssignmentViewHolder(View itemView) {
            super(itemView);
            tvAssignmentTitle = itemView.findViewById(R.id.tv_assignment_title);
            tvStartDate = itemView.findViewById(R.id.tv_start_date);
            tvEndDate = itemView.findViewById(R.id.tv_end_date);
        }
    }
}
