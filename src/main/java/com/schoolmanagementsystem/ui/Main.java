package com.schoolmanagementsystem.ui;

import com.schoolmanagementsystem.util.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseHelper.initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/schoolmanagementsystem/MainDash.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Image image = new Image("com/schoolmanagementsystem/background.jpeg");
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, false, false) // Stretches to fill region
        );
        Background background = new Background(backgroundImage);
        StackPane root = new StackPane();
        root.setBackground(background);
        stage.setTitle("School Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}