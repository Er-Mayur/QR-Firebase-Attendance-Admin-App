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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Objects;

public class AttendanceGatingActivity extends AppCompatActivity {

    private String textAcademicYear, textDate, textYearSelected, textSEMSelected, textSelectSelected, textSubjectSelected;
    private Spinner spnYear, spnSelect, spnSEM, spnSubject;
    private EditText edtAcademicYear, edtDate;
    private Button btnTakeAttendance;
    private boolean isFormatting;
    private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_gating_activity);
        getSupportActionBar().setTitle("Get Attendance");

        edtAcademicYear = findViewById(R.id.edt_academic_year);
        edtDate = findViewById(R.id.edt_date);
        btnTakeAttendance = findViewById(R.id.btn_take_attendance);

        // Initialize the Spinner
        spnYear = findViewById(R.id.spn_year);
        spnSelect = findViewById(R.id.spn_select);
        spnSEM = findViewById(R.id.spn_SEM);
        spnSubject = findViewById(R.id.spn_subject);

        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSubject.setAdapter(subjectAdapter);
        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSubjectSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AttendanceGatingActivity.this, "Please select subject", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> selectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.Select_list, android.R.layout.simple_spinner_item);
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
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spnSubject.setAdapter(subjectAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AttendanceGatingActivity.this, "Please select option", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> semAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM_list, android.R.layout.simple_spinner_item);
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
                Toast.makeText(AttendanceGatingActivity.this, "Please select SEM", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.Year_list, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnYear.setAdapter(yearAdapter);
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textYearSelected = adapterView.getItemAtPosition(i).toString();
                ArrayAdapter<CharSequence> adapter;
                if (Objects.equals(textYearSelected, "FE")){
                    adapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.FE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "SE")) {
                    adapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "TE")) {
                    adapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.TE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "BE")) {
                    adapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.BE_SEM_list, android.R.layout.simple_spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM_list, android.R.layout.simple_spinner_item);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spnSEM.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AttendanceGatingActivity.this, "Please select Year", LENGTH_SHORT).show();
            }
        });

        edtAcademicYear.setFilters(new InputFilter[]{new AttendanceGatingActivity.DateFormatFilter()});
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
        //Setting Up DatePicker on edtDOB
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtDate.setError(null);
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker
                picker = new DatePickerDialog(AttendanceGatingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        edtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        btnTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textAcademicYear = edtAcademicYear.getText().toString();
                textDate = edtDate.getText().toString();

                //Checking information is Not empty
                if (TextUtils.isEmpty(textAcademicYear) || textAcademicYear.length() != 7) {
                    edtAcademicYear.setError("Enter Academic Year 202X-2X");
                    edtAcademicYear.requestFocus();
                } else if (TextUtils.isEmpty(textDate)) {
                    edtDate.setError("Enter Current Date");
                    edtDate.requestFocus();
                } else if (Objects.equals(textYearSelected, "Year")) {
                    Toast.makeText(AttendanceGatingActivity.this, "Please select Year", LENGTH_SHORT).show();
                    spnYear.requestFocus();
                } else if (Objects.equals(textSEMSelected, "SEM")) {
                    Toast.makeText(AttendanceGatingActivity.this, "Please select SEM", LENGTH_SHORT).show();
                    spnSEM.requestFocus();
                } else if (Objects.equals(textSelectSelected, "Select")) {
                    Toast.makeText(AttendanceGatingActivity.this, "Please select", LENGTH_SHORT).show();
                    spnSelect.requestFocus();
                } else if (Objects.equals(textSubjectSelected, "Subject")) {
                    Toast.makeText(AttendanceGatingActivity.this, "Please select subject", LENGTH_SHORT).show();
                    spnSubject.requestFocus();
                } else {
                    Intent intent = new Intent(AttendanceGatingActivity.this, AttendanceDataActivity.class);
                    // Put the data to pass as extras in the Intent
                    intent.putExtra("textAcademicYear", textAcademicYear);
                    intent.putExtra("textDate", textDate.replace("/","-"));
                    intent.putExtra("textYearSelected", textYearSelected);
                    intent.putExtra("textSEMSelected", textSEMSelected);
                    intent.putExtra("textSelectSelected", textSelectSelected);
                    intent.putExtra("textSubjectSelected", textSubjectSelected);
                    startActivity(intent);
                }
            }
        });
    }
    private void subjectList(){
        ArrayAdapter<CharSequence> subjectAdapter = null;
        if(!Objects.equals(textYearSelected, "Year") && !Objects.equals(textSelectSelected, "Select") && !Objects.equals(textSEMSelected, "SEM")){
            if (Objects.equals(textSelectSelected, "Lecture")){
                if (Objects.equals(textSEMSelected, "SEM-1")){
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM1_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM2_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM3_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM4_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM5_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM6_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM7_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM8_Subject_list, android.R.layout.simple_spinner_item);
                }
            } else if (Objects.equals(textSelectSelected, "Practical")){
                if (Objects.equals(textSEMSelected, "SEM-1")){
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM1_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM2_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM3_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM4_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM5_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM6_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM7_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                    subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.SEM8_Practical_list, android.R.layout.simple_spinner_item);
                }
            }
        }
        else {
            subjectAdapter = ArrayAdapter.createFromResource(AttendanceGatingActivity.this, R.array.Subject_list, android.R.layout.simple_spinner_item);
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