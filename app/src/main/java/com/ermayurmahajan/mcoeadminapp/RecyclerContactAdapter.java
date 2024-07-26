package com.ermayurmahajan.mcoeadminapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> {
    Context context;
    String textPresentAbsent;
    ArrayList<ListModel> arrayContactModel;
    private boolean isOperationOne = true;
    RecyclerContactAdapter(Context context, ArrayList<ListModel> arrayContactModel){
        this.context = context;
        this.arrayContactModel = arrayContactModel;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.student_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtRollNo.setText(arrayContactModel.get(position).textRollNo);
        holder.txtStudentFullName.setText(arrayContactModel.get(position).textStudentFullName);
        holder.txtBatch.setText(arrayContactModel.get(position).textBatch);
        holder.txtStudentMobileNumber.setText(arrayContactModel.get(position).textStudentMobileNumber);
        int textColor;
        if ("Present".equals(arrayContactModel.get(position).textPresentAbsent)) {
            textColor = context.getResources().getColor(android.R.color.holo_green_dark);
        } else {
            textColor = context.getResources().getColor(android.R.color.holo_red_dark);
        }

        setTextViewColor(holder.txtRollNo, textColor);
        setTextViewColor(holder.txtStudentFullName, textColor);
        setTextViewColor(holder.txtBatch, textColor);
        setTextViewColor(holder.txtStudentMobileNumber, textColor);
    }
    private void setTextViewColor(TextView textView, int color) {
        textView.setTextColor(color);
    }
    @Override
    public int getItemCount() {
        return arrayContactModel.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtRollNo, txtStudentFullName, txtBatch, txtStudentMobileNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRollNo = itemView.findViewById(R.id.txt_roll_no);
            txtStudentFullName = itemView.findViewById(R.id.txt_student_full_name);
            txtBatch = itemView.findViewById(R.id.txt_batch);
            txtStudentMobileNumber = itemView.findViewById(R.id.txt_student_mobile_number);
        }
    }
}
