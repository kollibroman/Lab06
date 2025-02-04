package org.filip.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.filip.Request.GetRequest;
import org.filip.Request.OrderRequest;
import org.filip.Request.SetRequest;
import org.filip.parser.RequestParser;
import org.filip.parser.RequestSerializer;
import org.filip.sockets.House;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HouseUI extends Application
{
    private House house;
    private Label volumeLabel;
    private ProgressBar volumeProgressBar;
    private Button pauseResumeButton;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        var rand = new Random();
        house = new House(rand.nextInt(100,4000), 100, "localhost", 9001);

        // Setup UI
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // House ID Label
        Label houseIdLabel = new Label("House #" + house.getPort());
        houseIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Volume Progress Bar
        volumeProgressBar = new ProgressBar(0);
        volumeProgressBar.setPrefWidth(300);

        // Volume Label
        volumeLabel = new Label("Current Volume: 0 / " + house.getMaxSewage());

        // Status Label
        statusLabel = new Label("Status: Running");

        // Pause/Resume Button
        pauseResumeButton = new Button("Pause");
        pauseResumeButton.setOnAction(e -> togglePause());

        root.getChildren().addAll(
                houseIdLabel,
                volumeProgressBar,
                volumeLabel,
                statusLabel,
                pauseResumeButton
        );

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Sewage Tank Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start simulation and UI updates
        house.startServer();
        house.startSimulation();

        startUIUpdates();

    }

    private void startUIUpdates() {
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // Update every second

                    Platform.runLater(() -> {
                        int currentVolume = house.getSewage();
                        int capacity = house.getMaxSewage();

                        // Update volume label and progress bar
                        volumeLabel.setText(String.format("Current Volume: %d / %d", currentVolume, capacity));
                        volumeProgressBar.setProgress((double) currentVolume / capacity);

                        // Update status based on pause state
                        statusLabel.setText(house.isPaused() ? "Status: Paused (Waiting for Emptying)" : "Status: Running");

                        // Update pause/resume button text
                        pauseResumeButton.setText(house.isPaused() ? "Resume" : "Pause");
                    });
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void togglePause()
    {
        if (house.isPaused())
        {
            house.resume();
        }
        else
        {
            house.pause();
        }
    }
}
