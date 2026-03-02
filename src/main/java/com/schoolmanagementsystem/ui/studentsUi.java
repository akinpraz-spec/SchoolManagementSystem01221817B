package com.schoolmanagementsystem.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.schoolmanagementsystem.domain.Student;
import com.schoolmanagementsystem.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.io.IOException;

public class studentsUi {
    @FXML private TableView<Student> studentTable;
    @FXML private Label statusLabel;
    @FXML private Button editbtn;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private final StudentService service;
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    public studentsUi() {
        // Initialize Service
        this.service = new StudentService();
    }

    @FXML
    private TableColumn<Student,String> studentIdColumn;

    @FXML
    private TableColumn<Student,String> fullNameColumn;

    @FXML
    private TableColumn<Student,String> programmeColumn;

    @FXML
    private TableColumn<Student,Integer> levelColumn;

    @FXML
    private TableColumn<Student,Double> gpaColumn;

    @FXML
    private TableColumn<Student,String> emailColumn;

    @FXML
    private TableColumn<Student, Integer> phoneColumn;

    @FXML
    private TableColumn<Student,String> dateColumn;

    @FXML
    private TableColumn<Student,String> statusColumn;

    @FXML
    public void initialize() {
        // Link the list to the table
        studentTable.setItems(studentList);
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<Student,String>("StudentId"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("fullName"));
        programmeColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("programme"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<Student, Integer>("level"));
        gpaColumn.setCellValueFactory(new PropertyValueFactory<Student, Double>("gpa"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Student, String> ("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Student, Integer>("phoneNumber"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<Student, String> ("DateAdded"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Student, String> ("status"));
        loadData();
    }

    @FXML
    public void homebtn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/schoolmanagementsystem/MainDash.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void loadData() {
        try {
            studentList.clear();
            studentList.addAll(service.fetchAllStudents());
            statusLabel.setText("Data loaded successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/schoolmanagementsystem/add_student.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Student");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks dashboard until closed
            stage.showAndWait();

            loadData(); // Refresh the table after the dialog closes
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDelete() {
        // 1. Get the selected student
        Student selected = studentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Selection Required", "Please select a student from the table first.");
            return;
        }

        // 2. Confirmation Dialog (Required for marks!)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete " + selected.getFullName() + "?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                service.removeStudent(selected.getStudentId());
                loadData(); // Refresh table
                statusLabel.setText("Student deleted successfully.");
            } catch (Exception e) {
                showError("Delete Error", e.getMessage());
            }
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML private TextField searchField;

    @FXML
    private void handleSearch() {
        try {
            String query = searchField.getText();
            studentList.setAll(service.search(query));
        } catch (Exception e) {
            statusLabel.setText("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        // ... selection check remains the same ...

        try {
            // CHANGE THIS LINE:
            // Use /com/sms/ instead of /org/sms/ to match your actual folder structure
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/schoolmanagementsystem/add_student.fxml"));
            Parent root = loader.load();

            AddStudentController controller = loader.getController();
            controller.setStudentData(selected);

            Stage stage = new Stage();
            stage.setTitle("Edit Student");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(studentTable.getScene().getWindow());
            stage.showAndWait();

            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            // This is where you see "Location is not set" because the file wasn't found
        }
    }
}
