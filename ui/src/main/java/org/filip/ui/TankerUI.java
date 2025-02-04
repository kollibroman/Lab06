package org.filip.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.filip.sockets.Tanker;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;


public class TankerUI extends Application
{
    private Tanker tanker;
    private ListView<String> orderListView;
    private int tankerId;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tanker");

        orderListView = new ListView<>();
        VBox layout = new VBox(10);
        layout.getChildren().add(orderListView);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        var thread = new Thread(() ->
        {
            initializeTanker();
            Platform.runLater(() ->
            {
                updateOrderList("Tanker #" + tankerId + " is ready to serve");
            });
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void initializeTanker()
    {
        try
        {
            int port = new Random().nextInt(1000, 9999);
            int maxCapacity = new Random().nextInt(300, 1000);
            tanker = new Tanker(port, "localhost", 9001, "localhost", 9003, maxCapacity);

            tanker.registerInOffice();
            tankerId = tanker.getCurrentTankerNumber();

            tanker.startServer();

            updateOrderList("Ready to Serve");

            startOrderProcessing();

        }
        catch (Exception e)
        {
            updateOrderList("Init Error: " + e.getMessage());
        }
    }

    private void startOrderProcessing() {
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Tanker: Looking for orders");

                    Thread.sleep(5000);

                } catch (Exception e) {
                    updateOrderList("Processing Error: " + e.getMessage());
                }
            }
        }).start();
    }

    private void updateOrderList(String message) {
        Platform.runLater(() -> {
            orderListView.getItems().add(0, message);
            if (orderListView.getItems().size() > 10) {
                orderListView.getItems().remove(10);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
