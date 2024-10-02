package com.ermayurmahajan.mcoeadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassMainActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ArrayList<ClassListModel> arrayListModel = new ArrayList<>();
    RecyclerView recyclerView;
    ClassRecyclerContactAdapter adapter;
    private LottieAnimationView loading;
    String currentTeacherUID;

    FloatingActionButton flotAddClassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.class_main_activity);
        getSupportActionBar().setTitle("Classroom");

        authProfile = FirebaseAuth.getInstance();
        currentTeacherUID = authProfile.getCurrentUser().getUid(); // Get the UID of the logged-in teacher

        recyclerView = findViewById(R.id.recycler_view);
        flotAddClassButton = findViewById(R.id.flot_add_class_button);
        loading = findViewById(R.id.loading);


        recyclerView.setLayoutManager(new LinearLayoutManager(ClassMainActivity.this));
        adapter = new ClassRecyclerContactAdapter(ClassMainActivity.this, arrayListModel);
        recyclerView.setAdapter(adapter);

        // Fetch classrooms created by the current teacher
        fetchClassrooms(currentTeacherUID);

        flotAddClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClassMainActivity.this , CreateClassroomActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_drawer_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.change_password_menu:
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                authProfile.signOut();
                Toast.makeText(ClassMainActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(ClassMainActivity.this, MainActivity.class);
                intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent5);
                finish(); // to close UserProfile Activity Activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void fetchClassrooms(String teacherUID) {
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classroom");
        classRef.orderByChild("teacherUID").equalTo(teacherUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListModel.clear(); // Clear previous data

                if (dataSnapshot.exists()) {
                    for (DataSnapshot classroomSnapshot : dataSnapshot.getChildren()) {
                        // Assuming your ClassroomDetails class has the same structure
                        String className = classroomSnapshot.child("className").getValue(String.class);
                        String details = classroomSnapshot.child("details").getValue(String.class);
                        String year = classroomSnapshot.child("year").getValue(String.class);
                        String subjectName = classroomSnapshot.child("subject").getValue(String.class);
                        String academicYear = classroomSnapshot.child("academicYear").getValue(String.class);
                        String classroomID = classroomSnapshot.getKey(); // Get the classroom ID

                        // Create a ClassListModel instance and add it to the arrayListModel
                        arrayListModel.add(new ClassListModel(classroomID, className, details, year,subjectName,academicYear));// Modify according to your ClassListModel constructor
                        loading.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                } else {
                    loading.setVisibility(View.GONE);
                    arrayListModel.add(new ClassListModel("Class Code", "Class Name", "Details", "Year","Subject Name", "20XX-2X"));// Modify according to your ClassListModel constructor
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                    Toast.makeText(ClassMainActivity.this, "No classrooms found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.setVisibility(View.GONE);
                Toast.makeText(ClassMainActivity.this, "Failed to retrieve classrooms: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Fetch classrooms when the activity is resumed
        fetchClassrooms(currentTeacherUID);
    }
}