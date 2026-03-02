package com.schoolmanagementsystem.repository;

import com.schoolmanagementsystem.domain.ProgrammeStats;
import com.schoolmanagementsystem.domain.Student;
import com.schoolmanagementsystem.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqliteStudentRepository implements StudentRepository {

    @Override
    public void addStudent(Student s) throws Exception {
        String sql = "INSERT INTO students (student_id, full_name, programme, level, gpa, email, phone_number, date_added, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getStudentId());
            pstmt.setString(2, s.getFullName());
            pstmt.setString(3, s.getProgramme());
            pstmt.setInt(4, s.getLevel());
            pstmt.setDouble(5, s.getGpa());
            pstmt.setString(6, s.getEmail());
            pstmt.setString(7, s.getPhoneNumber());
            pstmt.setString(8, s.getDateAdded());
            pstmt.setString(9, s.getStatus());
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<Student> getAllStudents() throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("date_added"),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    @Override
    public void updateStudent(Student s) throws Exception {
        String sql = "UPDATE students SET full_name = ?, programme = ?, level = ?, gpa = ?, " +
                "email = ?, phone_number = ?, status = ? WHERE student_id = ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getFullName());
            pstmt.setString(2, s.getProgramme());
            pstmt.setInt(3, s.getLevel());
            pstmt.setDouble(4, s.getGpa());
            pstmt.setString(5, s.getEmail());
            pstmt.setString(6, s.getPhoneNumber());
            pstmt.setString(7, s.getStatus());
            pstmt.setString(8, s.getStudentId()); // The Primary Key
            pstmt.executeUpdate();
        }
    }
    @Override
    public void deleteStudent(String id) throws Exception {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }
    @Override
    public List<Student> searchStudents(String q) throws Exception {
        List<Student> list = new ArrayList<>();
        // Search by ID or Name using % wildcard for partial matches
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ?";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + q + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                            rs.getString("student_id"), rs.getString("full_name"),
                            rs.getString("programme"), rs.getInt("level"),
                            rs.getDouble("gpa"), rs.getString("email"),
                            rs.getString("phone_number"), rs.getString("date_added"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }


    public List<Student> getTopStudents(String progFilter, Integer levelFilter) throws Exception {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (progFilter != null && !progFilter.equals("All")) {
            sql.append(" AND programme = ?");
            params.add(progFilter);
        }
        if (levelFilter != null && levelFilter != 0) {
            sql.append(" AND level = ?");
            params.add(levelFilter);
        }
        sql.append(" ORDER BY gpa DESC LIMIT 10");

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToStudent(rs));
            }
        }
        return list;
    }


    public List<Student> getAtRiskStudents(double threshold) throws Exception {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE gpa < ? ORDER BY gpa ASC";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, threshold);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToStudent(rs));
            }
        }
        return list;
    }


    public Map<String, Integer> getGpaDistribution() throws Exception {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("0.0 - 1.0", 0); dist.put("1.0 - 2.0", 0);
        dist.put("2.0 - 3.0", 0); dist.put("3.0 - 4.0", 0);
        dist.put("4.0 - 5.0", 0);

        String sql = "SELECT gpa FROM students";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                double g = rs.getDouble("gpa");
                if (g < 1.0) dist.put("0.0 - 1.0", dist.get("0.0 - 1.0") + 1);
                else if (g < 2.0) dist.put("1.0 - 2.0", dist.get("1.0 - 2.0") + 1);
                else if (g < 3.0) dist.put("2.0 - 3.0", dist.get("2.0 - 3.0") + 1);
                else if (g < 4.0) dist.put("3.0 - 4.0", dist.get("3.0 - 4.0") + 1);
                else dist.put("4.0 - 5.0", dist.get("4.0 - 5.0") + 1);
            }
        }
        return dist;
    }


    public List<ProgrammeStats> getProgrammeStats() throws Exception {
        List<ProgrammeStats> stats = new ArrayList<>();
        String sql = "SELECT programme, COUNT(*) as total, AVG(gpa) as avg_gpa FROM students GROUP BY programme";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.add(new ProgrammeStats(
                        rs.getString("programme"),
                        rs.getInt("total"),
                        rs.getDouble("avg_gpa")
                ));
            }
        }
        return stats;
    }

    // Helper to avoid code duplication
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"), rs.getString("full_name"),
                rs.getString("programme"), rs.getInt("level"),
                rs.getDouble("gpa"), rs.getString("email"),
                rs.getString("phone_number"), rs.getString("date_added"),
                rs.getString("status")
        );
    }
}
