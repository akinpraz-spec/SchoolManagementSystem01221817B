package com.schoolmanagementsystem.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:data/student_db.db";

    public static Connection connect() throws SQLException {
        // Ensure data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        //  Student ID must be primary key, NOT NULL constraints, CHECK constraints
        String sql = "CREATE TABLE IF NOT EXISTS students ("
                + "student_id TEXT PRIMARY KEY, "
                + "full_name TEXT NOT NULL, "
                + "programme TEXT NOT NULL, "
                + "level INTEGER CHECK(level IN (100, 200, 300, 400, 500, 600, 700)), "
                + "gpa REAL CHECK(gpa >= 0.0 AND gpa <= 5.0), "
                + "email TEXT, "
                + "phone_number TEXT, "
                + "date_added TEXT, "
                + "status TEXT CHECK(status IN ('Active', 'Inactive'))"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
    }
}
