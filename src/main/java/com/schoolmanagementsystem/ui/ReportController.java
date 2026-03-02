package com.schoolmanagementsystem.ui;

import com.schoolmanagementsystem.domain.ProgrammeStats;
import com.schoolmanagementsystem.domain.Student;
import com.schoolmanagementsystem.service.StudentService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class ReportController {
    // Tab 1: Top Performers
    @FXML private TableView<Student> topTable;
    @FXML private TableColumn<Student, Integer> rankCol;
    @FXML private ComboBox<String> progFilter;
    @FXML private ComboBox<Integer> levelFilter;

    // Tab 2: At Risk
    @FXML private TableView<Student> riskTable;
    @FXML private TextField thresholdField;

    // Tab 3: Charts
    @FXML private BarChart<String, Number> gpaChart;

    // Tab 4: Summary
    @FXML private TableView<ProgrammeStats> summaryTable;

    private final StudentService service = new StudentService();

    @FXML
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize() {
        // Initialize Filters
        progFilter.getItems().addAll("All", "Computer Science", "Information Technology", "Business Info Systems");
        progFilter.getSelectionModel().selectFirst();

        levelFilter.getItems().add(0); // 0 acts as "All"
        levelFilter.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
        levelFilter.getSelectionModel().selectFirst();

        // Initialize Rank Column
        rankCol.setCellValueFactory(col ->
                new SimpleIntegerProperty(topTable.getItems().indexOf(col.getValue()) + 1).asObject()
        );

        // Load all data
        loadTopPerformers();
        loadAtRisk();
        loadDistribution();
        loadSummary();
    }

    @FXML
    private void loadTopPerformers() {
        try {
            String prog = progFilter.getValue();
            Integer level = levelFilter.getValue();
            // Handle "All" selection
            if ("All".equals(prog)) prog = null;
            if (level != null && level == 0) level = null;

            topTable.setItems(FXCollections.observableArrayList(
                    service.getTopStudents(prog, level)
            ));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void loadAtRisk() {
        try {
            double threshold = Double.parseDouble(thresholdField.getText());
            riskTable.setItems(FXCollections.observableArrayList(
                    service.getAtRiskStudents(threshold)
            ));
        } catch (Exception e) {
            // Ignore invalid number input for now
        }
    }

    private void loadDistribution() {
        try {
            gpaChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Students");

            Map<String, Integer> data = service.getGpaDistribution();
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            gpaChart.getData().add(series);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadSummary() {
        try {
            summaryTable.setItems(FXCollections.observableArrayList(
                    service.getProgrammeStats()
            ));
        } catch (Exception e) { e.printStackTrace(); }
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
}
