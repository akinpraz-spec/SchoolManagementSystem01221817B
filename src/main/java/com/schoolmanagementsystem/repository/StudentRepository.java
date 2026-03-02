package com.schoolmanagementsystem.repository;

import com.schoolmanagementsystem.domain.ProgrammeStats;
import com.schoolmanagementsystem.domain.Student;

import java.util.List;
import java.util.Map;

public interface StudentRepository {
    void addStudent(Student student) throws Exception;
    List<Student> getAllStudents() throws Exception;
    void updateStudent(Student student) throws Exception;
    void deleteStudent(String studentId) throws Exception;
    List<Student> searchStudents(String query) throws Exception;
    Map<String, Integer> getGpaDistribution() throws Exception;
    List<ProgrammeStats> getProgrammeStats() throws Exception;
    List<Student> getAtRiskStudents(double threshold) throws Exception;
    List<Student> getTopStudents(String prog, Integer level) throws Exception;
}
