package com.ermayurmahajan.mcoeadminapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseDatabase database;
    private ReadWriteDetails readUserDetails;
    private DatabaseReference attendanceRef, registerRef, absent;

    private static final int SMS_PERMISSION_REQUEST_CODE = 123;

    // TODO: Rename and change types of parameters
    private String mParam1, mParam2;
    private String textAcademicYear, textCurrentDate, textYearSelected, classroomID,textClassName, textDetails, textSem, textSubject;
    private LinearLayout studentListLiner, attendanceListLiner, smsSendLiner;
    private static final String studentListGoogleSheetLink = "https://docs.google.com/spreadsheets/d/1UsfPK9B3ioue1nR2U0vNJ6TUZfXLua-1UHK7pe6uz1c/edit?usp=sharing";

    public ClassInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassInfoFragment newInstance(String param1, String param2) {
        ClassInfoFragment fragment = new ClassInfoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.class_info_fragment, container, false);


        // Retrieve the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {

            textClassName = arguments.getString("textClassName");
            textAcademicYear = arguments.getString("textAcademicYear");
            textYearSelected = arguments.getString("textYear");
            textDetails = arguments.getString("textDetails");
            textSem = arguments.getString("textSem");
            textSubject = arguments.getString("textSubject");
            classroomID = arguments.getString("classroomID");
            // Use the retrieved values as needed
        }

        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        textCurrentDate = (day + "-" + (month + 1) + "-" + year);

        FirebaseApp.initializeApp(getContext());
        database = FirebaseDatabase.getInstance();
        registerRef = database.getReference("Registered Students");
        attendanceRef = database.getReference("Classroom").child(classroomID).child("Attendance").child(textCurrentDate);

        studentListLiner = view.findViewById(R.id.student_list_liner);
        attendanceListLiner = view.findViewById(R.id.attendance_list_liner);
        smsSendLiner = view.findViewById(R.id.sms_send_liner);

        studentListLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 //Create an Intent with the ACTION_VIEW action and the Google Sheet URI
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(studentListGoogleSheetLink));
                // Start the browser activity to open the Google Sheet
                startActivity(intent);
            }
        });
        attendanceListLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create an Intent with the ACTION_VIEW action and the Google Sheet URI
                Intent intent = new Intent(getActivity(), AttendanceDisplayActivity.class);
                intent.putExtra("textClassName", textClassName);
                intent.putExtra("textAcademicYear", textAcademicYear);
                intent.putExtra("textYearSelected", textYearSelected);
                intent.putExtra("textSem", textSem);
                intent.putExtra("textDetails", textDetails);
                intent.putExtra("textSubject", textSubject);
                intent.putExtra("classroomID", classroomID);

                startActivity(intent);
            }
        });
        smsSendLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSmsPermission()) {
                    getAbsentStudent();
                } else {
                    requestSmsPermission();
                }
            }
        });

        return view;
    }
    private void getAbsentStudent(){
        absent = attendanceRef.child("Absent");
        absent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String studentId = studentSnapshot.getKey();

                        if (studentSnapshot.getValue().toString().equals("false")){

                            DatabaseReference studentRef = registerRef.child(textAcademicYear).child(textYearSelected).child(studentId);
                            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot studentDataSnapshot) {
                                    if (studentDataSnapshot.exists()) {
                                        ReadWriteDetails readUserDetails = studentDataSnapshot.getValue(ReadWriteDetails.class);
                                        String studentName =  readUserDetails.textStudentFullName;
                                        String parentNumber = "+91"+readUserDetails.textParentMobileNumber;

                                        sendSMSToParent(parentNumber, studentName);
                                        Toast.makeText(getContext(), "SMS send successfully to absent student", Toast.LENGTH_LONG).show();
                                        // After SMS is sent, update the SMSStatus to true
                                        absent.child(studentId).setValue(true).addOnCompleteListener(task -> {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Failed to update SMS status for student: " + studentName, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else {
                            Toast.makeText(getContext(), "SMS is already sent", Toast.LENGTH_LONG).show();
                        }

                    }
                }else {
                    Toast.makeText(getContext(), "Wrong details / No absent Students for this Year", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendSMSToParent(String parentNumber, String studentName) {
        String smsMessage = "Dear Parent,\nYour child "+studentName+" was absent for today's "+textSubject+" lecture (date: "+textCurrentDate+" ).\nThank you - PES Modern College Pune";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(parentNumber, null, smsMessage, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAbsentStudent();
            } else {
                Toast.makeText(getContext(), "SMS Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}