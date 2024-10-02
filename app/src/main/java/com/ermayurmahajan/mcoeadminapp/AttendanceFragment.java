package com.ermayurmahajan.mcoeadminapp;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendanceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase database;
    private DatabaseReference attendanceRef, registerRef, present, absent, studentIDToStoreAbsent;
    private String textAcademicYear, textCurrentDate, textYearSelected, classroomID;
    private Button btnAttendanceDone;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    MediaPlayer mediaPlayer;
    private DecoratedBarcodeView barcodeView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendance_fragment, container, false);

        // Retrieve the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            classroomID = arguments.getString("classroomID");
            textAcademicYear = arguments.getString("textAcademicYear");
            textYearSelected = arguments.getString("textYear");


            // Use the retrieved values as needed
        }

        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();

        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        textCurrentDate = (day + "-" + (month + 1) + "-" + year);



        attendanceRef = database.getReference("Classroom").child(classroomID).child("Attendance");
        registerRef = database.getReference("Registered Students");

        present = attendanceRef.child(textCurrentDate).child("Present");
        absent = attendanceRef.child(textCurrentDate).child("Absent");

        studentIDToStoreAbsent = registerRef.child(textAcademicYear).child(textYearSelected);

        barcodeView = view.findViewById(R.id.QR_scanner);
        btnAttendanceDone = view.findViewById(R.id.btn_attendance_done);
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep_sound);


        btnAttendanceDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAbsentStudent();
                Intent intent = new Intent(getActivity(), ClassMainActivity.class);
                startActivity(intent);
            }
        });


        // check permission
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            startCamera();
        }

        return view;
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
                                    Toast.makeText(getContext(), "Student marked present.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle the case when the student is not registered (e.g., show an error message)
                                    Toast.makeText(getContext(), "Invalid QR OR Wrong Class.", Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        setAbsentStudent();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

}