package com.ermayurmahajan.mcoeadminapp;

public class AssignmentListModel {
    private String title;
    private String description;
    private String startDate;
    private String endDate;

    // Constructor
    public AssignmentListModel(String title, String description, String startDate, String endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
