package com.schoolmanagementsystem.domain;

import javafx.beans.property.*;

public class ProgrammeStats {
    private final StringProperty programme;
    private final IntegerProperty totalStudents;
    private final DoubleProperty averageGpa;

    public ProgrammeStats(String programme, int total, double avg) {
        this.programme = new SimpleStringProperty(programme);
        this.totalStudents = new SimpleIntegerProperty(total);
        this.averageGpa = new SimpleDoubleProperty(avg);
    }

    public String getProgramme() { return programme.get(); }
    public int getTotalStudents() { return totalStudents.get(); }
    public double getAverageGpa() { return averageGpa.get(); }
}