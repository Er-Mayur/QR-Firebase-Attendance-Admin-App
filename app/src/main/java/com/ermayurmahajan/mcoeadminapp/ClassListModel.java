package com.ermayurmahajan.mcoeadminapp;

public class ClassListModel {
    String textClassCode , textClassName, textDetails, textYear, textSubjectName ,textAcademicYear;

    public ClassListModel(String textClassCode, String textClassName, String textDetails, String textYear, String textSubjectName, String textAcademicYear){
        this.textClassCode = textClassCode;
        this.textSubjectName = textSubjectName;
        this.textClassName = textClassName;
        this.textDetails = textDetails;
        this.textYear = textYear;
        this.textAcademicYear = textAcademicYear;
    }
}
