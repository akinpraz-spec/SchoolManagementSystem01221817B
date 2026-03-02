package com.schoolmanagementsystem.service;

import com.schoolmanagementsystem.domain.ProgrammeStats;
import com.schoolmanagementsystem.domain.Student;
import com.schoolmanagementsystem.repository.SqliteStudentRepository;
import com.schoolmanagementsystem.repository.StudentRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class StudentService {
    private final StudentRepository repository;

    public StudentService() {

        this.repository = new SqliteStudentRepository();
    }

    public void registerStudent(Student student) throws Exception {
        // 1. Validation Logic (Strict requirement!)
        validateStudent(student);

        // 2. Check for Duplicate ID
        List<Student> existing = repository.getAllStudents();
        boolean exists = existing.stream()
                .anyMatch(s -> s.getStudentId().equalsIgnoreCase(student.getStudentId()));

        if (exists) {
            throw new IllegalArgumentException("Student ID already exists!");
        }

        // 3. Save to Database
        repository.addStudent(student);
    }

    private void validateStudent(Student s) {
        if (s.getStudentId() == null || s.getStudentId().isBlank()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (s.getGpa() < 0.0 || s.getGpa() > 5.1) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 5.0.");
        }
        if (s.getFullName() == null || s.getFullName().split(" ").length < 2) {
            throw new IllegalArgumentException("Please provide a full name (First and Last).");
        }
        // Add more rules for Level (100-700) and Email here
    }

    public List<Student> fetchAllStudents() throws Exception {
        return repository.getAllStudents();
    }

    public void removeStudent(String id) throws Exception {
        if (id == null || id.isEmpty()) throw new Exception("Invalid Student ID");
        repository.deleteStudent(id);
    }

    public List<Student> search(String query) throws Exception {
        // If search is empty, just return everyone
        if (query == null || query.trim().isEmpty()) {
            return repository.getAllStudents();
        }
        return repository.searchStudents(query);
    }

    public void updateStudentInfo(Student student) throws Exception {
        // Reuse the same validation logic used for adding
        validateStudent(student);

        repository.updateStudent(student);
    }

    // Add these methods to your Service class
    public List<Student> getTopStudents(String prog, Integer level) throws Exception {
        return repository.getTopStudents(prog, level);
    }

    public List<Student> getAtRiskStudents(double threshold) throws Exception {
        return repository.getAtRiskStudents(threshold);
    }

    public Map<String, Integer> getGpaDistribution() throws Exception {
        return repository.getGpaDistribution();
    }

    public List<ProgrammeStats> getProgrammeStats() throws Exception {
        return repository.getProgrammeStats();
    }

    public void exportReport(String reportType) throws Exception {
        // 1. Ensure 'data' folder exists
        File dataFolder = new File("data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs(); // Creates the directory if missing
        }

        // 2. Determine which data to fetch and what to name the file
        List<Student> students;
        String filename;

        switch (reportType) {
            case "TOP_PERFORMERS":

                students = repository.getTopStudents(null, null);
                filename = "top_performers.csv";
                break;
            case "AT_RISK":

                students = repository.getAtRiskStudents(2.0);
                filename = "at_risk_students.csv";
                break;
            case "FULL_LIST":
            default:
                students = repository.getAllStudents();
                filename = "all_students.csv";
                break;
        }


        File exportFile = new File(dataFolder, filename);


        try (PrintWriter writer = new PrintWriter(exportFile)) {

            writer.println("Student ID,Full Name,Programme,Level,GPA,Email,Phone,Date Added,Status");

            for (Student s : students) {
                writer.printf("%s,%s,%s,%d,%.2f,%s,%s,%s,%s%n",
                        s.getStudentId(),
                        escapeCsv(s.getFullName()), // Helper to handle commas in names
                        escapeCsv(s.getProgramme()),
                        s.getLevel(),
                        s.getGpa(),
                        s.getEmail(),
                        s.getPhoneNumber(),
                        s.getDateAdded(),
                        s.getStatus());
            }
        }
    }

    // Helper to prevent CSV breaking if a name contains a comma
    private String escapeCsv(String data) {
        if (data != null && data.contains(",")) {
            return "\"" + data + "\"";
        }
        return data;
    }

    public void importFromCSV(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip the header row (Student ID, Full Name, etc.)
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Split the row by commas
                String[] data = line.split(",");

                // Ensure the row has all 9 columns before trying to save it
                if (data.length >= 9) {
                    Student s = new Student();
                    s.setStudentId(data[0].trim());

                    // Remove quotes from names/programmes if they were added during export
                    s.setFullName(data[1].replace("\"", "").trim());
                    s.setProgramme(data[2].replace("\"", "").trim());

                    s.setLevel(Integer.parseInt(data[3].trim()));
                    s.setGpa(Double.parseDouble(data[4].trim()));
                    s.setEmail(data[5].trim());
                    s.setPhoneNumber(data[6].trim());
                    s.setDateAdded(data[7].trim());
                    s.setStatus(data[8].trim());

                    try {
                        // Try to add the student as a new record
                        repository.addStudent(s);
                    } catch (Exception e) {
                        // If adding fails (e.g., the Student ID already exists in the database),
                        // update the existing record instead!
                        repository.updateStudent(s);
                    }
                }
            }
        }
    }
}
