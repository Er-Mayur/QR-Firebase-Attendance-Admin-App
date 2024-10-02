package com.ermayurmahajan.mcoeadminapp;

import java.util.HashMap;
import java.util.Map;

public class TeacherReadWriteDetails{
    public String textTeacherFullName, textDOB, textGenderSelected, textTeacherMobileNumber, textEmail;
    public Map<String, Boolean> classroomIDs;  // Map to store multiple classroom IDs as Boolean

    public TeacherReadWriteDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(TeacherReadWriteDetails.class)
        classroomIDs = new HashMap<>();
    }

    public TeacherReadWriteDetails(String textTeacherFullName, String textDOB, String textGenderSelected, String textTeacherMobileNumber, String textEmail) {
        this.textTeacherFullName = textTeacherFullName;
        this.textDOB = textDOB;
        this.textGenderSelected = textGenderSelected;
        this.textTeacherMobileNumber = textTeacherMobileNumber;
        this.textEmail = textEmail;
        this.classroomIDs = new HashMap<>();  // Initialize the map
    }

    // Method to add classroom ID to the map
    public void addClassroomID(String classroomID) {
        this.classroomIDs.put(classroomID, true);  // Use Boolean value
    }
}
