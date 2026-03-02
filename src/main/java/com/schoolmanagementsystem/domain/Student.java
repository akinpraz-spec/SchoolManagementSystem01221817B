package com.schoolmanagementsystem.domain;

import javafx.beans.property.*;

public class Student {
    private final StringProperty studentId;
    private final StringProperty fullName;
    private final StringProperty programme;
    private final IntegerProperty level;
    private final DoubleProperty gpa;
    private final StringProperty email;
    private final StringProperty phoneNumber;
    private final StringProperty dateAdded;
    private final StringProperty status;

    // Default Constructor
    public Student() {
        this.studentId = new SimpleStringProperty();
        this.fullName = new SimpleStringProperty();
        this.programme = new SimpleStringProperty();
        this.level = new SimpleIntegerProperty();
        this.gpa = new SimpleDoubleProperty();
        this.email = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();
        this.dateAdded = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
    }

    // Constructor with all fields
    public Student(String studentId, String fullName, String programme, int level,
                   double gpa, String email, String phoneNumber, String dateAdded, String status) {
        this.studentId = new SimpleStringProperty(studentId);
        this.fullName = new SimpleStringProperty(fullName);
        this.programme = new SimpleStringProperty(programme);
        this.level = new SimpleIntegerProperty(level);
        this.gpa = new SimpleDoubleProperty(gpa);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.dateAdded = new SimpleStringProperty(dateAdded);
        this.status = new SimpleStringProperty(status);
    }

    public String getStudentId() { return studentId.get(); }
    public String getFullName() { return fullName.get(); }
    public String getProgramme() { return programme.get(); }
    public int getLevel() { return level.get(); }
    public double getGpa() { return gpa.get(); }
    public String getEmail() { return email.get(); }
    public String getPhoneNumber() { return phoneNumber.get(); }
    public String getDateAdded() { return dateAdded.get(); }
    public String getStatus() { return status.get(); }

    // Setters
    public void setStudentId(String value) { studentId.set(value); }
    public void setFullName(String value) { fullName.set(value); }
    public void setProgramme(String value) { programme.set(value); }
    public void setLevel(int value) { level.set(value); }
    public void setGpa(double value) { gpa.set(value); }
    public void setEmail(String value) { email.set(value); }
    public void setPhoneNumber(String value) { phoneNumber.set(value); }
    public void setDateAdded(String value) { dateAdded.set(value); }
    public void setStatus(String value) { status.set(value); }
}
