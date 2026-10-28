package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    // Database configuration
    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/jdbc";

    private static final String USER = "root";

    // Read password from environment variable
    private static final String PASSWORD =
            System.getenv("my_password");

    // Database connection
    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "DB_PASSWORD environment variable is not set. "
                            + "Check IntelliJ Run Configuration."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // CREATE: Insert student
    public static void insertStudent(
            int id, String name, int age,
            String gender, String aadhar) throws SQLException {

        String sql = "INSERT INTO student "
                + "(id, name, age, gender, aadhar) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, age);
            ps.setString(4, gender);
            ps.setString(5, aadhar);

            ps.executeUpdate();
            System.out.println("Student inserted successfully!");
        }
    }

    // READ: Display all students
    public static void displayStudents() throws SQLException {

        String sql = "SELECT id, name, age, gender, aadhar FROM student";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nStudent Details:");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println(
                        "ID: " + rs.getInt("id")
                                + ", Name: " + rs.getString("name")
                                + ", Age: " + rs.getInt("age")
                                + ", Gender: " + rs.getString("gender")
                                + ", Aadhar: " + rs.getString("aadhar")
                );
            }

            if (!found) {
                System.out.println("No student records found.");
            }
        }
    }

    // UPDATE: Update student age using ID
    public static void updateStudent(int id, int age)
            throws SQLException {

        String sql = "UPDATE student SET age = ? WHERE id = ?";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, age);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("Student ID not found!");
            }
        }
    }

    // DELETE: Delete student using ID
    public static void deleteStudent(int id) throws SQLException {

        String sql = "DELETE FROM student WHERE id = ?";

        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student ID not found!");
            }
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        // Test database connection
        try (Connection cn = getConnection()) {
            System.out.println("MySQL connected successfully!");
            System.out.println("Database: " + cn.getCatalog());

        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            System.out.println("Reason: " + e.getMessage());
            return;
        }

        try {
            // Check whether student ID 1 already exists
            boolean exists;

            String checkSql =
                    "SELECT id FROM student WHERE id = ?";

            try (Connection cn = getConnection();
                 PreparedStatement ps = cn.prepareStatement(checkSql)) {

                ps.setInt(1, 1);

                try (ResultSet rs = ps.executeQuery()) {
                    exists = rs.next();
                }
            }

            // CREATE
            if (!exists) {
                insertStudent(
                        1, "Siri", 21, "Female", "123456789019"
                );
            } else {
                System.out.println(
                        "Student ID 1 already exists; skipping insert."
                );
            }

            // READ
            displayStudents();

            // UPDATE
            updateStudent(1, 22);

            // READ updated records
            displayStudents();

            // DELETE: Uncomment only when you want to delete ID 1
            // deleteStudent(1);

        } catch (SQLException e) {
            System.out.println("CRUD operation failed!");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
