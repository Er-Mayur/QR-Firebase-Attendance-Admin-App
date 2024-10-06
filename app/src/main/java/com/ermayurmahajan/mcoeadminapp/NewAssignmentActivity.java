package com.ermayurmahajan.mcoeadminapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewAssignmentActivity extends AppCompatActivity {
    private String textClassName, textAcademicYear, textDate, textYearSelected, textSubjectSelected, textSelectSelected, textSEMSelected, classroomID;
    private String textAssignmentTitle, textAssignmentDescription, textStartDate, textEndDate;
    private static final int PICK_MEDIA_REQUEST = 1;
    private EditText edtAssignmentTitle, edtAssignmentDescription, edtStartDate, edtEndDate;
    private Button btnSelectMedia, btnAddAssignment;
    private RecyclerView rvSelectedMedia;
    private MediaRecyclerAdapter mediaAdapter;
    private List<Uri> selectedMediaUris;
    private Calendar startCalendar, endCalendar;

    private LottieAnimationView loading;



    private DatabaseReference classroomRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_assignment_activity);
        getSupportActionBar().setTitle("New Assignment");

        // Get the Intent that started this activity
        Intent intent = getIntent();

        textClassName = intent.getStringExtra("textClassName");
        textAcademicYear = intent.getStringExtra("textAcademicYear");
        textDate = intent.getStringExtra("textDate");
        textYearSelected = intent.getStringExtra("textYear");
        textSEMSelected = intent.getStringExtra("textSem");
        textSelectSelected = intent.getStringExtra("textDetails");
        textSubjectSelected = intent.getStringExtra("textSubject");
        classroomID = intent.getStringExtra("classroomID");


        edtAssignmentTitle = findViewById(R.id.edt_assignment_title);
        edtAssignmentDescription = findViewById(R.id.edt_assignment_description);
        edtStartDate = findViewById(R.id.edt_start_date);
        edtEndDate = findViewById(R.id.edt_end_date);
        btnSelectMedia = findViewById(R.id.btn_select_media);
        rvSelectedMedia = findViewById(R.id.rv_selected_media);
        btnAddAssignment = findViewById(R.id.btn_add_assignment);
        loading = findViewById(R.id.loading);


        // Initialize selected media list and adapter
        selectedMediaUris = new ArrayList<>();
        mediaAdapter = new MediaRecyclerAdapter(selectedMediaUris);
        rvSelectedMedia.setLayoutManager(new LinearLayoutManager(this));
        rvSelectedMedia.setAdapter(mediaAdapter);


        // Date picker dialogs for start date and end date
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        edtStartDate.setOnClickListener(v -> showDatePickerDialog(edtStartDate, startCalendar));
        edtEndDate.setOnClickListener(v -> showDatePickerDialog(edtEndDate, endCalendar));

        //Initialize Firebase Realtime Database and Storage
        FirebaseApp.initializeApp(this);

        classroomRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classroomID);
        storageRef = FirebaseStorage.getInstance().getReference("Classroom").child(classroomID).child("Assignments");

        // Set a click listener for selecting media files
        btnSelectMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMediaPicker();
            }
        });
        // Set click listener for adding assignment
        btnAddAssignment.setOnClickListener(v -> {
            // Save assignment details to Firebase
            textAssignmentTitle = edtAssignmentTitle.getText().toString().trim();
            textAssignmentDescription = edtAssignmentDescription.getText().toString().trim();
            textStartDate = edtStartDate.getText().toString().trim();
            textEndDate = edtEndDate.getText().toString().trim();

            // Checking information is Not empty
            if (TextUtils.isEmpty(textAssignmentTitle)) {
                edtAssignmentTitle.setError("Enter Assignment Title");
                edtAssignmentTitle.requestFocus();
            } else if (TextUtils.isEmpty(textAssignmentDescription)) {
                edtAssignmentDescription.setError("Enter Assignment Description");
                edtAssignmentDescription.requestFocus();
            } else if (TextUtils.isEmpty(textStartDate)) {
                edtStartDate.setError("Select Start Date");
                edtStartDate.requestFocus();
            } else if (TextUtils.isEmpty(textEndDate)) {
                edtEndDate.setError("Select End Date");
                edtEndDate.requestFocus();
            } else {
                loading.setVisibility(View.VISIBLE);
                saveAssignmentToFirebase();
            }

        });
    }

    // Save assignment data and media files to Firebase
    private void saveAssignmentToFirebase() {


        // Create a unique ID for the assignment
        String assignmentId = generateAssignmentId();

        // Store assignment details in Firebase Realtime Database
        Map<String, Object> assignmentData = new HashMap<>();
        assignmentData.put("assignmentId", assignmentId);
        assignmentData.put("textAssignmentTitle", textAssignmentTitle);
        assignmentData.put("textAssignmentDescription", textAssignmentDescription);
        assignmentData.put("textStartDate", textStartDate);
        assignmentData.put("textEndDate", textEndDate);

        // Add the assignment under the classroom
        classroomRef.child("Assignments").child(assignmentId).setValue(assignmentData)
                .addOnSuccessListener(aVoid -> {
                    // If media files are selected, upload them to Firebase Storage
                    if (!selectedMediaUris.isEmpty()) {
                        uploadMediaFiles(assignmentId);
                    } else {
                        loading.setVisibility(View.GONE);
                        finish();
                        Toast.makeText(this, "Assignment added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add assignment", Toast.LENGTH_SHORT).show());
    }
    // Upload selected media files to Firebase Storage
    private void uploadMediaFiles(String assignmentId) {
        for (Uri mediaUri : selectedMediaUris) {
            String fileName = mediaUri.getLastPathSegment();
            StorageReference fileRef = storageRef.child(assignmentId).child(fileName);

            fileRef.putFile(mediaUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Save the download URL of the file to the assignment in Firebase Realtime Database
                classroomRef.child("Assignments").child(assignmentId).child("mediaFiles").push().setValue(uri.toString())
                        .addOnSuccessListener(aVoid -> Log.d("FirebaseStorage", "File uploaded and URL saved successfully"))
                        .addOnFailureListener(e -> Log.e("FirebaseStorage", "Failed to save file URL: " + e.getMessage()));
            })).addOnFailureListener(e -> Log.e("FirebaseStorage", "Failed to upload file: " + e.getMessage()));
        }
        loading.setVisibility(View.GONE);
        Toast.makeText(this, "Media files uploaded successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Date picker dialog for start and end date selection
    private void showDatePickerDialog(EditText editText, Calendar calendar) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(NewAssignmentActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                editText.setText(sdf.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    // Open the file picker to choose media files
    private void openMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*"); // Allows all file types; you can restrict to "image/*", "application/pdf", etc.
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple file selection
        startActivityForResult(Intent.createChooser(intent, "Select Media Files"), PICK_MEDIA_REQUEST);
    }

    // Handle the result of the media picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple files selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        selectedMediaUris.add(fileUri);
                    }
                } else if (data.getData() != null) {
                    // Single file selected
                    Uri fileUri = data.getData();
                    selectedMediaUris.add(fileUri);
                }

                // Notify the adapter that data has changed to update the RecyclerView
                mediaAdapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show();
        }
    }
    // Method to generate classroom ID
    @NonNull
    private String generateAssignmentId() {
        Calendar calendar = Calendar.getInstance();
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String year = String.format("%02d", calendar.get(Calendar.YEAR) % 100); // Last two digits of the year
        String hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minutes = String.format("%02d", calendar.get(Calendar.MINUTE));
        String seconds = String.format("%02d", calendar.get(Calendar.SECOND));

        return day + month + year + hours + minutes + seconds;
    }
}