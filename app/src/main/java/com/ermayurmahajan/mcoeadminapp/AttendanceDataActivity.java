package com.ermayurmahajan.mcoeadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttendanceDataActivity extends AppCompatActivity {
    ArrayList<ListModel> arrayListModel = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerContactAdapter adapter;
    private ReadWriteDetails readUserDetails;
    private FirebaseDatabase database;
    private DatabaseReference attendanceRef, registerRef, present, absent, studentIDToData;
    private String textAcademicYear, textDate, textYearSelected, textSubjectSelected, textSelectSelected, textSEMSelected;
    private String textStudentFullName, textRollNo, textBatch, textStudentMobileNumber;
    private TextView txtDetails, txtNoRecord;
    private LottieAnimationView loading;
    boolean isPresent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_data_activity);
        getSupportActionBar().setTitle("Attendance Data");

        recyclerView = findViewById(R.id.recycler_view);
        txtDetails = findViewById(R.id.txt_details);
        txtNoRecord = findViewById(R.id.txt_no_record);
        loading = findViewById(R.id.loading);

        recyclerView.setLayoutManager(new LinearLayoutManager(AttendanceDataActivity.this));

        // Get the Intent that started this activity
        Intent intent = getIntent();
        textAcademicYear = intent.getStringExtra("textAcademicYear");
        textDate = intent.getStringExtra("textDate");
        textYearSelected = intent.getStringExtra("textYearSelected");
        textSEMSelected = intent.getStringExtra("textSEMSelected");
        textSelectSelected = intent.getStringExtra("textSelectSelected");
        textSubjectSelected = intent.getStringExtra("textSubjectSelected");

        txtDetails.setText(textAcademicYear+" "+textYearSelected+" "+textSEMSelected+" "+textSelectSelected+" "+textSubjectSelected+" "+textDate.replace("-","/"));

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("Attendance");
        registerRef = database.getReference("Registered Students");
        present = attendanceRef.child(textSelectSelected).child(textAcademicYear).child(textYearSelected).child(textSEMSelected).child(textSubjectSelected).child(textDate).child("Present");
        absent = attendanceRef.child(textSelectSelected).child(textAcademicYear).child(textYearSelected).child(textSEMSelected).child(textSubjectSelected).child(textDate).child("Absent");
        studentIDToData = registerRef.child(textAcademicYear).child(textYearSelected);
        setAbsentStudent();
    }
    private void setAbsentStudent(){
        arrayListModel.clear();
        present.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot presentDataSnapshot) {
                for (DataSnapshot studentSnapshot : presentDataSnapshot.getChildren()) {
                    isPresent = true;
                    String studentId = studentSnapshot.getKey();
                    setStudentIDToData(studentId, "Present");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
            }
        });
        absent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot presentDataSnapshot) {
                if (presentDataSnapshot.exists()) {
                    for (DataSnapshot studentSnapshot : presentDataSnapshot.getChildren()) {
                        String studentId = studentSnapshot.getKey();
                        setStudentIDToData(studentId, "Absent");
                    }
                }
                else if (!isPresent){
                    loading.setVisibility(View.GONE);
                    txtNoRecord.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
            }
        });
    }
    private void setStudentIDToData(String studentId, String textPresentAbsent){
        studentIDToData.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readUserDetails = dataSnapshot.getValue(ReadWriteDetails.class);
                if (readUserDetails != null) {
                    // Set data
                    textRollNo = readUserDetails.textRollNo;
                    textStudentFullName = readUserDetails.textStudentFullName;
                    textBatch = readUserDetails.textBatch;
                    textStudentMobileNumber = readUserDetails.textStudentMobileNumber;
                    arrayListModel.add(new ListModel(textRollNo,textStudentFullName,textBatch,textStudentMobileNumber, textPresentAbsent));
                    adapter = new RecyclerContactAdapter(AttendanceDataActivity.this, arrayListModel);
                    recyclerView.setAdapter(adapter);
                    // HashSet to store present student IDs
                    loading.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}