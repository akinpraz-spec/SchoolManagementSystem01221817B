package com.schoolmanagementsystem.ui;

import com.schoolmanagementsystem.service.StudentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private Stage stage;
    private Scene scene;
    private Parent root;



    @FXML
    private Button studentsbtn;
    @FXML
    private Button reportbtn;
    @FXML
    private Button importbtn;
    @FXML
    private Button exportbtn;
    @FXML
    private Button settingbtn;

    @FXML
    private Label totalstudentslbl;
    @FXML
    private Label activestudentslbl;
    @FXML
    private Label inactivestudentslbl;
    @FXML
    private Label avggpalbl;
    @FXML
    private ImageView logo;
    //private StudentService service;
    private final com.schoolmanagementsystem.service.StudentService service = new com.schoolmanagementsystem.service.StudentService();

    @FXML
    public void initialize() {
        loadDashboardData();
    }
    private void loadDashboardData() {
        try {
            // These methods should be implemented in your StudentService/Repository
            int total = service.fetchAllStudents().size();
            long active = service.fetchAllStudents().stream().filter(s -> "Active".equalsIgnoreCase(s.getStatus())).count();
            long inactive = total - active;
            double avgGpa = service.fetchAllStudents().stream().mapToDouble(s -> s.getGpa()).average().orElse(0.0);

            totalstudentslbl.setText(String.valueOf(total));
            activestudentslbl.setText(String.valueOf(active));
            inactivestudentslbl.setText(String.valueOf(inactive));
            avggpalbl.setText(String.format("%.2f", avgGpa));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void studentbtn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/schoolmanagementsystem/students.fxml"));
        Parent root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void reportbtn(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/schoolmanagementsystem/report.fxml")); // Check package path!
            Parent root = loader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show alert if file not found
        }
    }
    @FXML
    public void importbtn(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Student Data (CSV)");

        // Only allow selecting CSV files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        // Optional: Make it open your project's "data" folder by default if it exists
        File dataFolder = new File("data");
        if (dataFolder.exists()) {
            fileChooser.setInitialDirectory(dataFolder);
        }

        // Show the open dialog
        File file = fileChooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                service.importFromCSV(file);
                showInfo("Import Successful", "Student data was successfully imported from:\n" + file.getName());

                // NOTE: If you have a method to refresh the total student counts on the dashboard, call it here!
                // e.g., loadDashboardData();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Import Failed", "Could not import data. Please ensure the CSV format matches the export format.\nError: " + e.getMessage());
            }
        }
    }

    @FXML
    public void exportbtn(ActionEvent event) throws IOException {
        // Create a dialog with 3 choices
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Export Data");
        alert.setHeaderText("Select Report to Export");
        alert.setContentText("Files will be saved in the project 'data' folder.");

        ButtonType btnFull = new ButtonType("Full Student List");
        ButtonType btnTop = new ButtonType("Top Performers");
        ButtonType btnRisk = new ButtonType("At Risk Students");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnFull, btnTop, btnRisk, btnCancel);

        // Wait for user choice
        java.util.Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() != btnCancel) {
            String reportType = "FULL_LIST"; // Default

            if (result.get() == btnTop) {
                reportType = "TOP_PERFORMERS";
            } else if (result.get() == btnRisk) {
                reportType = "AT_RISK";
            }

            try {
                service.exportReport(reportType);
                showInfo("Export Successful", "Report saved to: data/" + reportType.toLowerCase() + ".csv");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Export Failed", "Could not save file: " + e.getMessage());
            }
        }
    }
    @FXML
    public void settingsbtn(ActionEvent event) throws IOException {
       /* FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();*/
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings ");
        alert.setHeaderText(null);
        alert.setContentText("Settings will be updated soon.");
        alert.showAndWait();
    }

    // --- Alert Helper Methods ---

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
