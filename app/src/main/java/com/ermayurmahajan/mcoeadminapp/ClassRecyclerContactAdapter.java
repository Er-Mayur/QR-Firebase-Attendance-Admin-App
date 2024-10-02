package com.ermayurmahajan.mcoeadminapp;


import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class ClassRecyclerContactAdapter extends RecyclerView.Adapter<ClassRecyclerContactAdapter.ViewHolder> {
    Context context;
    private ArrayList<ClassListModel> arrayContactModel;
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
        holder.cardClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Correctly create the Intent to start AdminMainActivity
                Intent intent = new Intent(context, AdminMainActivity.class);

                // Optionally, pass any data to the AdminMainActivity
                intent.putExtra("classroomID", arrayContactModel.get(position).textClassCode);
                intent.putExtra("textAcademicYear", arrayContactModel.get(position).textAcademicYear);
                intent.putExtra("textYear", arrayContactModel.get(position).textYear);
                intent.putExtra("textClassName", arrayContactModel.get(position).textClassName);


                context.startActivity(intent); // Start the AdminMainActivity
            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayContactModel.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtClassCode, txtClassName, txtSubjectName, txtYear,txt_details;
        CardView cardClass;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClassCode = itemView.findViewById(R.id.txt_classroomID);
            txtClassName = itemView.findViewById(R.id.txt_class_name);
            txt_details = itemView.findViewById(R.id.txt_details);
            txtSubjectName = itemView.findViewById(R.id.txt_subject_name);
            txtYear = itemView.findViewById(R.id.txt_year);
            cardClass = itemView.findViewById(R.id.main_card_container);
        }
    }
}
