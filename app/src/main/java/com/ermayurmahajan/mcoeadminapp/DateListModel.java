package com.ermayurmahajan.mcoeadminapp;

public class DateListModel {
    String date ,textClassCode , textClassName, textDetails, textYear, textSubjectName ,textAcademicYear, textSem;
    public DateListModel(){}
    // Constructor for DateModel
    public DateListModel(String date, String textClassCode, String textClassName, String textDetails, String textYear, String textSubjectName, String textAcademicYear, String textSem){
        this.textClassCode = textClassCode;
        this.textSubjectName = textSubjectName;
        this.textClassName = textClassName;
        this.textDetails = textDetails;
        this.textYear = textYear;
        this.textAcademicYear = textAcademicYear;
        this.textSem = textSem;
        this.date = date;
    }
}
