package org.filip.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.filip.sockets.SewagePlant;

import java.util.Map;

public class SewagePlantUI extends Application
{
    private SewagePlant sewagePlant;
    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        try {
            sewagePlant = new SewagePlant(9003, "localhost", 9000);
            sewagePlant.startServer();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error initializing SewagePlant: " + e.getMessage());
            return;
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // Output Area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);

        // Show Tanker Waste Section
        Button showWasteButton = new Button("Show Tanker Waste");
        showWasteButton.setOnAction(e -> showTankerWaste());

        root.getChildren().addAll(showWasteButton, outputArea);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Sewage Plant Management");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start periodic updates
        startUIUpdates();
        sewagePlant.startServer();
    }

    @SneakyThrows
    private void showTankerWaste() {
        Map<Integer, Integer> tankerWaste = sewagePlant.getTankerVolumes();
        StringBuilder output = new StringBuilder("Tanker Waste Summary:\n");

        for (Map.Entry<Integer, Integer> entry : tankerWaste.entrySet()) {
            output.append("Tanker ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" units\n");
        }
        appendOutput(output.toString());
    }

    private void startUIUpdates() {
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000); // Update every 2 seconds
                    Platform.runLater(this::showTankerWaste);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void appendOutput(String message) {
        Platform.runLater(() -> outputArea.appendText(message + "\n"));
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
