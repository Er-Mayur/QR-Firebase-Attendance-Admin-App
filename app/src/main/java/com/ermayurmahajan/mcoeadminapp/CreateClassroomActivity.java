package com.ermayurmahajan.mcoeadminapp;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateClassroomActivity extends AppCompatActivity {

    private String textAcademicYear, textClassName, textYearSelected, textSEMSelected, textSelectSelected, textSubjectSelected;
    private Spinner spnYear, spnSelect, spnSEM, spnSubject;
    private EditText edtAcademicYear, edtClassName;
    private Button btnCreateClassroom;
    private boolean isFormatting;
    private LottieAnimationView loading;
    String classroomID;
    DatabaseReference userRef;
    FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference teachersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_classroom_activity);
        getSupportActionBar().setTitle("Create Classroom");

        edtClassName = findViewById(R.id.edt_class_name);
        edtAcademicYear = findViewById(R.id.edt_academic_year);
        btnCreateClassroom = findViewById(R.id.btn_create_classroom);

        // Initialize the Spinner
        spnYear = findViewById(R.id.spn_year);
        spnSelect = findViewById(R.id.spn_select);
        spnSEM = findViewById(R.id.spn_SEM);
        spnSubject = findViewById(R.id.spn_subject);
        loading = findViewById(R.id.loading);


        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        teachersRef = database.getReference("Teachers");
        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSubject.setAdapter(subjectAdapter);
        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSubjectSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CreateClassroomActivity.this, "Please select subject", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> selectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.Select_list, android.R.layout.simple_spinner_item);
        selectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSelect.setAdapter(selectAdapter);
        spnSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSelectSelected = adapterView.getItemAtPosition(i).toString();
                ArrayAdapter<CharSequence> subjectAdapter;
                if (Objects.equals(textSelectSelected, "Lecture")){
                    subjectList();
                } else if (Objects.equals(textSelectSelected, "Practical")) {
                    subjectList();
                } else {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spnSubject.setAdapter(subjectAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CreateClassroomActivity.this, "Please select option", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> semAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM_list, android.R.layout.simple_spinner_item);
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
                Toast.makeText(CreateClassroomActivity.this, "Please select SEM", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.Year_list, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnYear.setAdapter(yearAdapter);
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textYearSelected = adapterView.getItemAtPosition(i).toString();
                ArrayAdapter<CharSequence> adapter;
                if (Objects.equals(textYearSelected, "FE")){
                    adapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.FE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "SE")) {
                    adapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "TE")) {
                    adapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.TE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "BE")) {
                    adapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.BE_SEM_list, android.R.layout.simple_spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM_list, android.R.layout.simple_spinner_item);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spnSEM.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(CreateClassroomActivity.this, "Please select Year", LENGTH_SHORT).show();
            }
        });

        edtAcademicYear.setFilters(new InputFilter[]{new CreateClassroomActivity.DateFormatFilter()});
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

        btnCreateClassroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textClassName = edtClassName.getText().toString();
                textAcademicYear = edtAcademicYear.getText().toString();

                // Checking information is Not empty
                if (TextUtils.isEmpty(textClassName)) {
                    edtClassName.setError("Enter Class Name");
                    edtClassName.requestFocus();
                } else if (TextUtils.isEmpty(textAcademicYear) || textAcademicYear.length() != 7) {
                    edtAcademicYear.setError("Enter Academic Year 202X-2X");
                    edtAcademicYear.requestFocus();
                } else if (Objects.equals(textYearSelected, "Year")) {
                    Toast.makeText(CreateClassroomActivity.this, "Please select Year", LENGTH_SHORT).show();
                    spnYear.requestFocus();
                } else if (Objects.equals(textSEMSelected, "SEM")) {
                    Toast.makeText(CreateClassroomActivity.this, "Please select SEM", LENGTH_SHORT).show();
                    spnSEM.requestFocus();
                } else if (Objects.equals(textSelectSelected, "Select")) {
                    Toast.makeText(CreateClassroomActivity.this, "Please select", LENGTH_SHORT).show();
                    spnSelect.requestFocus();
                } else if (Objects.equals(textSubjectSelected, "Subject")) {
                    Toast.makeText(CreateClassroomActivity.this, "Please select subject", LENGTH_SHORT).show();
                    spnSubject.requestFocus();
                } else {
                    loading.setVisibility(View.VISIBLE);
                    btnCreateClassroom.setEnabled(false);
                    // Call to generate classroom ID and create classroom
                    classroomID = generateClassroomId();
                    firebaseUser = auth.getCurrentUser();
                    userRef = teachersRef.child("AIML").child(firebaseUser.getUid());

                    // First, save the classroom data
                    DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classroomID);
                    classRef.child("className").setValue(textClassName);
                    classRef.child("academicYear").setValue(textAcademicYear);
                    classRef.child("year").setValue(textYearSelected);
                    classRef.child("details").setValue(textSelectSelected);
                    classRef.child("sem").setValue(textSEMSelected);
                    classRef.child("subject").setValue(textSubjectSelected);
                    classRef.child("teacherUID").setValue(firebaseUser.getUid());

                    // Then, add the classroom ID to the teacher's record as a Map<String, Boolean>
                    Map<String, Object> classroomMap = new HashMap<>();
                    classroomMap.put(classroomID, true);  // Use Boolean value to mark the classroom ID

                    userRef.child("classroomIDs").updateChildren(classroomMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Classroom created successfully, now update teacher's data
                                updateTeacherWithClassroomID(firebaseUser.getUid(), classroomID);
                                loading.setVisibility(View.GONE);
                                Toast.makeText(CreateClassroomActivity.this, "Classroom created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                loading.setVisibility(View.GONE);
                                Toast.makeText(CreateClassroomActivity.this, "Failed to update classroom IDs. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



    }
    // Method to generate classroom ID
    @NonNull
    private String generateClassroomId() {
        Calendar calendar = Calendar.getInstance();
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String year = String.format("%02d", calendar.get(Calendar.YEAR) % 100); // Last two digits of the year
        String hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minutes = String.format("%02d", calendar.get(Calendar.MINUTE));
        String seconds = String.format("%02d", calendar.get(Calendar.SECOND));

        return day + month + year + hours + minutes + seconds;
    }

    private void updateTeacherWithClassroomID(String teacherUID, String classroomID) {
        userRef = teachersRef.child("AIML").child(teacherUID);
        // Fetch current teacher details to add the new classroom ID
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TeacherReadWriteDetails teacherDetails = dataSnapshot.getValue(TeacherReadWriteDetails.class);
                    if (teacherDetails != null) {
                        // Add the new classroom ID to the teacher's map of classroom IDs
                        teacherDetails.classroomIDs.put(classroomID, true);  // Use Boolean value

                        // Update teacher's data in Firebase
                        userRef.setValue(teacherDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateClassroomActivity.this, "Classroom ID added to teacher data", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateClassroomActivity.this, "Failed to update teacher data. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(CreateClassroomActivity.this, "Teacher data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CreateClassroomActivity.this, "Error fetching teacher data.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void subjectList(){
        ArrayAdapter<CharSequence> subjectAdapter = null;
        if(!Objects.equals(textYearSelected, "Year") && !Objects.equals(textSelectSelected, "Select") && !Objects.equals(textSEMSelected, "SEM")){
            if (Objects.equals(textSelectSelected, "Lecture")){
                if (Objects.equals(textSEMSelected, "SEM-1")){
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM1_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM2_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM3_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM4_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM5_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM6_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM7_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM8_Subject_list, android.R.layout.simple_spinner_item);
                }
            } else if (Objects.equals(textSelectSelected, "Practical")){
                if (Objects.equals(textSEMSelected, "SEM-1")){
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM1_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM2_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM3_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM4_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM5_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM6_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM7_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                    subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.SEM8_Practical_list, android.R.layout.simple_spinner_item);
                }
            }
        }
        else {
            subjectAdapter = ArrayAdapter.createFromResource(CreateClassroomActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
        }
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSubject.setAdapter(subjectAdapter);
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
}