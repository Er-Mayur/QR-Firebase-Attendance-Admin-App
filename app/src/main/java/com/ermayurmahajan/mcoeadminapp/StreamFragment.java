package com.ermayurmahajan.mcoeadminapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StreamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String textAcademicYear, textCurrentDate, textYearSelected, classroomID,textClassName, textDetails, textSem, textSubject;
    TextView txtClassCode, txtClassName, txtSubjectName, txtYear,txt_details;

    private LottieAnimationView loading;


    private RecyclerView recyclerView;
    private AssignmentListRecyclerAdapter assignmentAdapter;
    private List<AssignmentListModel> assignmentList;
    private DatabaseReference databaseReference;


    FloatingActionButton flotAddClassButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StreamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StreamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StreamFragment newInstance(String param1, String param2) {
        StreamFragment fragment = new StreamFragment();
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
        View view = inflater.inflate(R.layout.stream_fragment, container, false);
        flotAddClassButton = view.findViewById(R.id.flot_add_class_button);
        loading = view.findViewById(R.id.loading);

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the list to hold assignment data
        assignmentList = new ArrayList<>();

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

        databaseReference = FirebaseDatabase.getInstance().getReference("Classroom").child(classroomID).child("Assignments");

        // Fetch assignments from Firebase
        fetchAssignmentsFromFirebase();

        txtClassCode = view.findViewById(R.id.txt_classroomID);
        txtClassName = view.findViewById(R.id.txt_class_name);
        txt_details = view.findViewById(R.id.txt_details);
        txtSubjectName = view.findViewById(R.id.txt_subject_name);
        txtYear = view.findViewById(R.id.txt_year);

        txtClassCode.setText(classroomID);
        txtClassName.setText(textClassName);
        txt_details.setText(textDetails);
        txtSubjectName.setText(textSubject);
        txtYear.setText(textYearSelected);

        flotAddClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), NewAssignmentActivity.class);
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


        return view;
    }
    private void fetchAssignmentsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing assignment list
                assignmentList.clear();
                loading.setVisibility(View.GONE);
                // Loop through each child node (assignment)
                for (DataSnapshot assignmentSnapshot : dataSnapshot.getChildren()) {
                    // Get assignment data
                    String title = assignmentSnapshot.child("textAssignmentTitle").getValue(String.class);
                    String description = assignmentSnapshot.child("textAssignmentDescription").getValue(String.class);
                    String startDate = assignmentSnapshot.child("textStartDate").getValue(String.class);
                    String endDate = assignmentSnapshot.child("textEndDate").getValue(String.class);

                    // Create an Assignment object and add it to the list
                    AssignmentListModel assignment = new AssignmentListModel(title, description, startDate, endDate);
                    assignmentList.add(assignment);
                }

                // Notify the adapter that data has changed
                assignmentAdapter = new AssignmentListRecyclerAdapter(assignmentList);
                recyclerView.setAdapter(assignmentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to fetch assignments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseDatabase", "Error: " + databaseError.getMessage());
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        // Fetch assignments from Firebase is resumed
        fetchAssignmentsFromFirebase();
    }
}