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

public class ClassRecyclerContactAdapter extends RecyclerView.Adapter<ClassRecyclerContactAdapter.ViewHolder> {
    Context context;
    private ArrayList<ClassListModel> arrayContactModel;
    private boolean isOperationOne = true;
    ClassRecyclerContactAdapter(Context context, ArrayList<ClassListModel> arrayContactModel){
        this.context = context;
        this.arrayContactModel = arrayContactModel;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.class_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtClassCode.setText(arrayContactModel.get(position).textClassCode);
        holder.txtClassName.setText(arrayContactModel.get(position).textClassName);
        holder.txtSubjectName.setText(arrayContactModel.get(position).textSubjectName);
        holder.txt_details.setText(arrayContactModel.get(position).textDetails);
        holder.txtYear.setText(arrayContactModel.get(position).textYear);

    }
    @Override
    public int getItemCount() {
        return arrayContactModel.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtClassCode, txtClassName, txtSubjectName, txtYear,txt_details;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClassCode = itemView.findViewById(R.id.txt_classroomID);
            txtClassName = itemView.findViewById(R.id.txt_class_name);
            txt_details = itemView.findViewById(R.id.txt_details);
            txtSubjectName = itemView.findViewById(R.id.txt_subject_name);
            txtYear = itemView.findViewById(R.id.txt_year);

        }
    }
}
