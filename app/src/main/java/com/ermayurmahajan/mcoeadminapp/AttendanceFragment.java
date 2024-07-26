package com.ermayurmahajan.mcoeadminapp;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;

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

import java.util.Calendar;
import java.util.Objects;

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


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String textAcademicYear, textCurrentDate, textYearSelected, textSEMSelected, textSelectSelected, textSubjectSelected;
    private Spinner spnYear, spnSelect, spnSEM, spnSubject;
    private EditText edtAcademicYear, edtCurrentDate;
    private Button btnTakeAttendance;
    private boolean isFormatting;

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

        edtAcademicYear = view.findViewById(R.id.edt_academic_year);
        edtCurrentDate = view.findViewById(R.id.edt_current_date);
        btnTakeAttendance = view.findViewById(R.id.btn_take_attendance);

        // Initialize the Spinner
        spnYear = view.findViewById(R.id.spn_year);
        spnSelect = view.findViewById(R.id.spn_select);
        spnSEM = view.findViewById(R.id.spn_SEM);
        spnSubject = view.findViewById(R.id.spn_subject);

        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Subject_list, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnSubject.setAdapter(subjectAdapter);
        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSubjectSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "Please select subject", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> selectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Select_list, android.R.layout.simple_spinner_item);
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
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Subject_list, android.R.layout.simple_spinner_item);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spnSubject.setAdapter(subjectAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "Please select option", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> semAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM_list, android.R.layout.simple_spinner_item);
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
                Toast.makeText(getActivity(), "Please select SEM", LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Year_list, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnYear.setAdapter(yearAdapter);
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textYearSelected = adapterView.getItemAtPosition(i).toString();
                ArrayAdapter<CharSequence> adapter;
                if (Objects.equals(textYearSelected, "FE")){
                    adapter = ArrayAdapter.createFromResource(requireContext(), R.array.FE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "SE")) {
                    adapter = ArrayAdapter.createFromResource(requireContext(), R.array.SE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "TE")) {
                    adapter = ArrayAdapter.createFromResource(requireContext(), R.array.TE_SEM_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textYearSelected, "BE")) {
                    adapter = ArrayAdapter.createFromResource(requireContext(), R.array.BE_SEM_list, android.R.layout.simple_spinner_item);
                } else {
                    adapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM_list, android.R.layout.simple_spinner_item);
                }
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spnSEM.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getActivity(), "Please select Year", LENGTH_SHORT).show();
            }
        });

        edtAcademicYear.setFilters(new InputFilter[]{new DateFormatFilter()});
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
        // Inflate the layout for this fragment

        btnTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textAcademicYear = edtAcademicYear.getText().toString();
                textCurrentDate = edtCurrentDate.getText().toString();

                //Checking information is Not empty
                if (TextUtils.isEmpty(textAcademicYear) || textAcademicYear.length() != 7) {
                    edtAcademicYear.setError("Enter Academic Year 202X-2X");
                    edtAcademicYear.requestFocus();
                } else if (TextUtils.isEmpty(textCurrentDate)) {
                    edtCurrentDate.setError("Enter Current Date");
                    edtCurrentDate.requestFocus();
                } else if (Objects.equals(textYearSelected, "Year")) {
                    Toast.makeText(requireContext(), "Please select Year", LENGTH_SHORT).show();
                    spnYear.requestFocus();
                } else if (Objects.equals(textSEMSelected, "SEM")) {
                    Toast.makeText(requireContext(), "Please select SEM", LENGTH_SHORT).show();
                    spnSEM.requestFocus();
                } else if (Objects.equals(textSelectSelected, "Select")) {
                    Toast.makeText(requireContext(), "Please select", LENGTH_SHORT).show();
                    spnSelect.requestFocus();
                } else if (Objects.equals(textSubjectSelected, "Subject")) {
                    Toast.makeText(requireContext(), "Please select subject", LENGTH_SHORT).show();
                    spnSubject.requestFocus();
                } else {
                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    // Put the data to pass as extras in the Intent
                    intent.putExtra("textAcademicYear", textAcademicYear);
                    intent.putExtra("textCurrentDate", textCurrentDate.replace("/","-"));
                    intent.putExtra("textYearSelected", textYearSelected);
                    intent.putExtra("textSEMSelected", textSEMSelected);
                    intent.putExtra("textSelectSelected", textSelectSelected);
                    intent.putExtra("textSubjectSelected", textSubjectSelected);
                    startActivity(intent);
                }
            }
        });
        return view;
    }
    private void subjectList(){
        ArrayAdapter<CharSequence> subjectAdapter = null;
        if(!Objects.equals(textYearSelected, "Year") && !Objects.equals(textSelectSelected, "Select") && !Objects.equals(textSEMSelected, "SEM")){
            if (Objects.equals(textSelectSelected, "Lecture")){
                if (Objects.equals(textSEMSelected, "SEM-1")){
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM1_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM2_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM3_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM4_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM5_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM6_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM7_Subject_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM8_Subject_list, android.R.layout.simple_spinner_item);
                }
            } else if (Objects.equals(textSelectSelected, "Practical")){
                if (Objects.equals(textSEMSelected, "SEM-1")){
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM1_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-2")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM2_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-3")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM3_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-4")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM4_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-5")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM5_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-6")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM6_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-7")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM7_Practical_list, android.R.layout.simple_spinner_item);
                } else if (Objects.equals(textSEMSelected, "SEM-8")) {
                    subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.SEM8_Practical_list, android.R.layout.simple_spinner_item);
                }
            }
        }
        else {
            subjectAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.Subject_list, android.R.layout.simple_spinner_item);
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