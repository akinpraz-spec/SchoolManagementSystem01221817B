package com.schoolmanagementsystem.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.schoolmanagementsystem.domain.Student;
import com.schoolmanagementsystem.service.StudentService;

import java.time.LocalDate;

public class AddStudentController {
    @FXML private Label headerLabel;
    @FXML private TextField idField, nameField, gpaField, emailField, phoneField;
    @FXML private ComboBox<String> programmeCombo;
    @FXML private ComboBox<Integer> levelCombo;

    private String originalDate;
    private String originalStatus;

    private final StudentService service = new StudentService();
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        programmeCombo.getItems().addAll("Computer Science", "Electrical/Electronics Engineering", "Medical Laboratory","Mechanical Engineering","Marketing","Civil Engineering");
        levelCombo.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
    }

    public void setStudentData(Student s) {
        this.isEditMode = true;
        headerLabel.setText("Edit Student Details");

        this.originalDate = s.getDateAdded();
        this.originalStatus = s.getStatus();

        // Disable ID field because Primary Keys should not change
        idField.setText(s.getStudentId());
        idField.setDisable(true);

        nameField.setText(s.getFullName());
        gpaField.setText(String.valueOf(s.getGpa()));
        programmeCombo.setValue(s.getProgramme());
        levelCombo.setValue(s.getLevel());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhoneNumber());
    }

    @FXML
    private void handleSave() {
        try {
            Student s = new Student();
            s.setStudentId(idField.getText());
            s.setFullName(nameField.getText());
            s.setProgramme(programmeCombo.getValue());
            s.setLevel(levelCombo.getValue() != null ? levelCombo.getValue() : 0);
            s.setGpa(Double.parseDouble(gpaField.getText().isEmpty() ? "0" : gpaField.getText()));
            s.setEmail(emailField.getText());
            s.setPhoneNumber(phoneField.getText());
            s.setStatus("Active");
            s.setDateAdded(LocalDate.now().toString());

            if (isEditMode) {
                s.setDateAdded(this.originalDate);
                s.setStatus(this.originalStatus);
                service.updateStudentInfo(s);
            } else {
                s.setDateAdded(LocalDate.now().toString());
                s.setStatus("Active"); // Default for new students
                service.registerStudent(s);
            }

            closeWindow();
        } catch (NumberFormatException e) {
            showAlert("Error", "GPA must be a valid number.");
        } catch (Exception e) {
            showAlert("Validation Error", e.getMessage());
        }
    }

    @FXML private void handleCancel() { closeWindow(); }

    private void closeWindow() {
        ((Stage) idField.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
