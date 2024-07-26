package com.ermayurmahajan.mcoeadminapp;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScannerActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference attendanceRef, registerRef, present, absent, studentIDToStoreAbsent;
    private String textAcademicYear, textCurrentDate, textYearSelected, textSubjectSelected, textSelectSelected, textSEMSelected;
    private Button btnAttendanceDone;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    MediaPlayer mediaPlayer;
    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_activity);
        getSupportActionBar().setTitle("Scanner");

        // Get the Intent that started this activity
        Intent intent = getIntent();
        textAcademicYear = intent.getStringExtra("textAcademicYear");
        textCurrentDate = intent.getStringExtra("textCurrentDate");
        textYearSelected = intent.getStringExtra("textYearSelected");
        textSEMSelected = intent.getStringExtra("textSEMSelected");
        textSelectSelected = intent.getStringExtra("textSelectSelected");
        textSubjectSelected = intent.getStringExtra("textSubjectSelected");

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("Attendance");
        registerRef = database.getReference("Registered Students");
        present = attendanceRef.child(textSelectSelected).child(textAcademicYear).child(textYearSelected).child(textSEMSelected).child(textSubjectSelected).child(textCurrentDate).child("Present");
        absent = attendanceRef.child(textSelectSelected).child(textAcademicYear).child(textYearSelected).child(textSEMSelected).child(textSubjectSelected).child(textCurrentDate).child("Absent");
        studentIDToStoreAbsent = registerRef.child(textAcademicYear).child(textYearSelected);

        barcodeView = findViewById(R.id.QR_scanner);
        btnAttendanceDone = findViewById(R.id.btn_attendance_done);
        mediaPlayer = MediaPlayer.create(ScannerActivity.this, R.raw.beep_sound);



        btnAttendanceDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAbsentStudent();
                Intent intent1 = new Intent(ScannerActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });



        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startCamera();
        }
    }

    private void startCamera() {
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(Collections.singleton(BarcodeFormat.QR_CODE)));
        Set<String> checkTheEntryOfscannedQRSet = new HashSet<>();
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    String scannedStudentId = result.getText(); // Scanned student ID from QR code
                    // Check if the QR code has been scanned before
                    if (!checkTheEntryOfscannedQRSet.contains(scannedStudentId)) {
                        checkTheEntryOfscannedQRSet.add(scannedStudentId);
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                        }
                        DatabaseReference studentIDIsExists = registerRef.child(textAcademicYear).child(textYearSelected).child(scannedStudentId);
                        studentIDIsExists.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Move the student ID from "Absent" to "Present" node
                                    DatabaseReference presentStudent = present.child(scannedStudentId);
                                    presentStudent.setValue(true);

                                    // Handle the case when the student is marked present (e.g., show a success message)
                                    Toast.makeText(ScannerActivity.this, "Student marked present.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle the case when the student is not registered (e.g., show an error message)
                                    Toast.makeText(ScannerActivity.this, "Invalid QR OR Wrong Class.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle the error if needed
                            }
                        });
                    }
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setAbsentStudent(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        studentIDToStoreAbsent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> absentStudents = new HashMap<>();

                // Retrieve present student IDs from the "Present" node
                present.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot presentDataSnapshot) {
                        // HashSet to store present student IDs
                        Set<String> presentStudentIds = new HashSet<>();

                        for (DataSnapshot studentSnapshot : presentDataSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();
                            presentStudentIds.add(studentId);
                        }

                        // Loop through the registered students and add to "Absent" if not present
                        for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                            String studentId = studentSnapshot.getKey();

                            // Check if the student ID is not in the presentStudentIds HashSet
                            if (!presentStudentIds.contains(studentId)) {
                                // Add the student ID to the map of absent students
                                absentStudents.put(studentId, true);
                            }
                        }

                        // Set the map of absent students as the value of "Absent" node
                        absent.setValue(absentStudents);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error if needed
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if needed
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setAbsentStudent();
    }
}
