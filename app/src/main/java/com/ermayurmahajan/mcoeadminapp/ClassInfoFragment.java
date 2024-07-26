package com.ermayurmahajan.mcoeadminapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

    // TODO: Rename and change types of parameters
    private String mParam1, mParam2;
    private LinearLayout studentListLiner, attendanceListLiner, smsSendLiner;
    private static final String studentListGoogleSheetLink = "https://docs.google.com/spreadsheets/d/1UsfPK9B3ioue1nR2U0vNJ6TUZfXLua-1UHK7pe6uz1c/edit?usp=sharing";
    private static final String attendanceListGoogleSheetLink = "https://docs.google.com/spreadsheets/d/1xu1noKSFcdJCh8aB4E-Plq3035e8yEL8sFVCNkNAohQ/edit?usp=sharing";
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
                Intent intent = new Intent(getActivity(), AttendanceGatingActivity.class);
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(attendanceListGoogleSheetLink));
                // Start the browser activity to open the Google Sheet
                startActivity(intent);
            }
        });
        smsSendLiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), SendSMSActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}