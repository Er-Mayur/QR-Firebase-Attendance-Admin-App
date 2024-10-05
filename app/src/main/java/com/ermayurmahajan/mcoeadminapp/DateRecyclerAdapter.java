package com.ermayurmahajan.mcoeadminapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DateRecyclerAdapter extends RecyclerView.Adapter<DateRecyclerAdapter.ViewHolder> {
    Context context;
    private ArrayList<DateListModel> dateList;

    // Constructor for DateRecyclerAdapter
    public DateRecyclerAdapter(Context context, ArrayList<DateListModel> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each date item
        View view = LayoutInflater.from(context).inflate(R.layout.date_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set the date in the TextView
        holder.txtDate.setText(dateList.get(position).date);
        holder.date_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Optionally, pass any data to the AdminMainActivity
                Intent intent = new Intent(context, AttendanceDataActivity.class);

                intent.putExtra("textClassName", dateList.get(position).textClassName);
                intent.putExtra("textAcademicYear", dateList.get(position).textAcademicYear);
                intent.putExtra("textYear", dateList.get(position).textYear);
                intent.putExtra("textDetails", dateList.get(position).textDetails);
                intent.putExtra("textSem", dateList.get(position).textSem);
                intent.putExtra("textSubject", dateList.get(position).textSubjectName);
                intent.putExtra("classroomID", dateList.get(position).textClassCode);
                intent.putExtra("textDate", dateList.get(position).date);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate;
        LinearLayout date_item_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Bind the TextView to show the date
            txtDate = itemView.findViewById(R.id.txt_date);
            date_item_layout = itemView.findViewById(R.id.date_item_layout);
        }
    }
}
