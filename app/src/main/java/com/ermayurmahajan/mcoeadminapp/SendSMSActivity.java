package com.ermayurmahajan.mcoeadminapp;

import static android.widget.Toast.LENGTH_SHORT;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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

import java.util.Calendar;
import java.util.Objects;

public class SendSMSActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference attendanceRef, registerRef, absent;
    private String textAcademicYear, textCurrentDate, textYearSelected, textSubjectSelected, textSEMSelected, textSMSDate;
    private Spinner spnYear, spnSEM, spnSubject;
    private Button btnSendSMS;
    private EditText edtAcademicYear, edtCurrentDate;
    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    private boolean isFormatting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_sms_activity);
        getSupportActionBar().setTitle("Send SMS");

        edtAcademicYear = findViewById(R.id.edt_academic_year);
        edtCurrentDate = findViewById(R.id.edt_current_date);
        btnSendSMS = findViewById(R.id.btn_send_SMS);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        attendanceRef = database.getReference("Attendance");
        registerRef = database.getReference("Registered Students");

        // Initialize the Spinner
        spnYear = findViewById(R.id.spn_year);
        spnSEM = findViewById(R.id.spn_SEM);
        spnSubject = findViewById(R.id.spn_subject);

        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSubject.setAdapter(subjectAdapter);
        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSubjectSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(SendSMSActivity.this, "Please select subject", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> semAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM_list, android.R.layout.simple_spinner_item);
        semAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSEM.setAdapter(semAdapter);
        spnSEM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSEMSelected = adapterView.getItemAtPosition(i).toString();
                subjectList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(SendSMSActivity.this, "Please select SEM", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.Year_list, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnYear.setAdapter(yearAdapter);
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textYearSelected = adapterView.getItemAtPosition(i).toString();
                ArrayAdapter<CharSequence> adapter;
                if (Objects.equals(textYearSelected, "FE")){
                    adapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.FE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "SE")) {
                    adapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "TE")) {
                    adapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.TE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "BE")) {
                    adapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.BE_SEM_list, android.R.layout.simple_spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM_list, android.R.layout.simple_spinner_item);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spnSEM.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(SendSMSActivity.this, "Please select Year", LENGTH_SHORT).show();
            }
        });
        edtAcademicYear.setFilters(new InputFilter[]{new SendSMSActivity.DateFormatFilter()});
        edtAcademicYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isFormatting) {
                    String inputText = s.toString();
                    String formattedText = formatDateText(inputText);

                    if (!inputText.equals(formattedText)) {
                        isFormatting = true;
                        s.replace(0, s.length(), formattedText);
                        isFormatting = false;
                    }
                }
            }
        });
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        edtCurrentDate.setText(day + "/" + (month + 1) + "/" + year);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textAcademicYear = edtAcademicYear.getText().toString();
                textSMSDate = edtCurrentDate.getText().toString();
                textCurrentDate = edtCurrentDate.getText().toString().replace("/","-");

                //Checking information is Not empty
                if (TextUtils.isEmpty(textAcademicYear) || textAcademicYear.length() != 7) {
                    edtAcademicYear.setError("Enter Academic Year 202X-2X");
                    edtAcademicYear.requestFocus();
                } else if (TextUtils.isEmpty(textCurrentDate)) {
                    edtCurrentDate.setError("Enter Current Date");
                    edtCurrentDate.requestFocus();
                } else if (Objects.equals(textYearSelected, "Year")) {
                    Toast.makeText(SendSMSActivity.this, "Please select Year", Toast.LENGTH_SHORT).show();
                    spnYear.requestFocus();
                } else if (Objects.equals(textSEMSelected, "SEM")) {
                    Toast.makeText(SendSMSActivity.this, "Please select SEM", LENGTH_SHORT).show();
                    spnSEM.requestFocus();
                } else if (Objects.equals(textSubjectSelected, "Subject")) {
                    Toast.makeText(SendSMSActivity.this, "Please select subject", LENGTH_SHORT).show();
                    spnSubject.requestFocus();
                } else if (checkSmsPermission()) {
                    getAbsentStudent();
                } else {
                    requestSmsPermission();
                }
            }
        });
    }

    private void subjectList(){
        ArrayAdapter<CharSequence> subjectAdapter = null;
        if(!Objects.equals(textYearSelected, "Year")  && !Objects.equals(textSEMSelected, "SEM")){
            if (Objects.equals(textSEMSelected, "SEM-1")){
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM1_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM2_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM3_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM4_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM5_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM6_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM7_Subject_list, android.R.layout.simple_spinner_item);
            } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.SEM8_Subject_list, android.R.layout.simple_spinner_item);
            }
        }
        else {
            subjectAdapter = ArrayAdapter.createFromResource(SendSMSActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
        }
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSubject.setAdapter(subjectAdapter);
    }
    private void getAbsentStudent(){

        absent = attendanceRef.child("Lecture").child(textAcademicYear).child(textYearSelected).child(textSEMSelected).child(textSubjectSelected).child(textCurrentDate).child("Absent");
        absent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String studentId = studentSnapshot.getKey();
                        DatabaseReference studentRef = registerRef.child(textAcademicYear).child(textYearSelected).child(studentId);
                        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot studentDataSnapshot) {
                                if (studentDataSnapshot.exists()) {
                                    ReadWriteDetails readUserDetails = studentDataSnapshot.getValue(ReadWriteDetails.class);
                                    String studentName =  readUserDetails.textStudentFullName;
                                    String parentNumber = "+91"+readUserDetails.textParentMobileNumber;
                                    sendSMSToParent(parentNumber, studentName);
                                    Toast.makeText(SendSMSActivity.this, "SMS send successfully to absent student", Toast.LENGTH_LONG).show();
                                    spnYear.setSelection(0);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else {
                    Toast.makeText(SendSMSActivity.this, "Wrong details / No absent Students for this Year", Toast.LENGTH_SHORT).show();
                    spnYear.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendSMSToParent(String parentNumber, String studentName) {
        String smsMessage = "Dear Parent,\nYour child "+studentName+" was absent for today's "+textSubjectSelected+" lecture (date: "+textSMSDate+" ).\nThank you - PES Modern College";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(parentNumber, null, smsMessage, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private String formatDateText(String text) {
        // Remove all non-numeric characters
        String numbersOnly = text.replaceAll("[^0-9]", "");

        if (numbersOnly.length() > 6) {
            // Truncate the input to a maximum of 7 characters
            numbersOnly = numbersOnly.substring(0, 6);
        }

        if (numbersOnly.length() >= 5) {
            // Format the date as "yyyy-yy"
            String formattedText = numbersOnly.substring(0, 4) + "-" + numbersOnly.substring(4);

            return formattedText;
        }

        return numbersOnly;
    }

    private class DateFormatFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder filteredBuilder = new StringBuilder(end - start);
            for (int i = start; i < end; i++) {
                char currentChar = source.charAt(i);
                if (Character.isDigit(currentChar) || currentChar == '-') {
                    filteredBuilder.append(currentChar);
                }
            }
            return filteredBuilder.toString();
        }
    }
    private boolean checkSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAbsentStudent();
            } else {
                Toast.makeText(this, "SMS Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}