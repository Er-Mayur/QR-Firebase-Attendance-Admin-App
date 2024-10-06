package com.ermayurmahajan.mcoeadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AttendanceDisplayActivity extends AppCompatActivity {

    private FirebaseDatabase database;

    private LottieAnimationView loading;


    private ArrayList<DateListModel> arrayListModel = new ArrayList<DateListModel>();
    RecyclerView recyclerView;
    DateRecyclerAdapter adapter;

    private DatabaseReference attendanceRef;


    private String textAcademicYear, textYear, classroomID,textClassName, textDetails, textSem, textSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.attendance_display_activity);
        getSupportActionBar().setTitle("Attendance Dates");

        recyclerView = findViewById(R.id.rvDates);
        // Set layout manager to display two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new DateRecyclerAdapter(AttendanceDisplayActivity.this, arrayListModel);
        recyclerView.setAdapter(adapter);


        loading = findViewById(R.id.loading);

        // Get the Intent that started this activity
        Intent intent = getIntent();

        textClassName = intent.getStringExtra("textClassName");
        textAcademicYear = intent.getStringExtra("textAcademicYear");
        textYear = intent.getStringExtra("textYearSelected");
        textDetails = intent.getStringExtra("textDetails");
        textSem = intent.getStringExtra("textSem");
        textSubject = intent.getStringExtra("textSubject");
        classroomID = intent.getStringExtra("classroomID");

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("Classroom").child(classroomID).child("Attendance");

        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListModel.clear(); // Clear previous data

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {

                    // Get the key (which is the date)
                    String date = dateSnapshot.getKey();
                    arrayListModel.add(new DateListModel(date, classroomID, textClassName, textDetails, textYear, textSubject, textAcademicYear, textSem));// Modify according to your ClassListModel constructor
                    loading.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes

                // Now you have a list of dates in dateList, you can pass it to your RecyclerView adapter or use it as needed

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors here
                Toast.makeText(AttendanceDisplayActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });


    }
}