package org.filip.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.filip.Request.OrderRequest;
import org.filip.sockets.Office;
import org.filip.ui.viewModel.HouseOrderViewModel;
import org.filip.ui.viewModel.TankerViewModel;
import org.filip.utils.TankerDetails;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class OfficeUI extends Application
{
    private Office office;
    private ObservableList<TankerViewModel> tankerData;
    private ObservableList<HouseOrderViewModel> houseOrderData;
    private TableView<TankerViewModel> tankerTableView;
    private TableView<HouseOrderViewModel> houseOrderTableView;

    // Track house orders separately
    private ConcurrentHashMap<String, LocalDateTime> pendingHouseOrders = new ConcurrentHashMap<>();

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        // Initialize the Office
        office = new Office(9000, "localhost", 9001);

        // Setup UI
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Sewage Management Office");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Tankers Section
        Label tankerLabel = new Label("Registered Tankers");
        tankerLabel.setStyle("-fx-font-weight: bold;");
        tankerData = FXCollections.observableArrayList();
        tankerTableView = createTankerTableView();

        // House Orders Section
        Label orderLabel = new Label("House Orders");
        orderLabel.setStyle("-fx-font-weight: bold;");
        houseOrderData = FXCollections.observableArrayList();
        houseOrderTableView = createHouseOrderTableView();

        root.getChildren().addAll(
                titleLabel,
                tankerLabel,
                tankerTableView,
                orderLabel,
                houseOrderTableView
        );

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("Sewage Tanker Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start periodic UI updates
        office.start();
        startUIUpdates();
    }

    private TableView<TankerViewModel> createTankerTableView() {
        TableView<TankerViewModel> tableView = new TableView<>();

        TableColumn<TankerViewModel, Integer> idColumn = new TableColumn<>("Tanker ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("tankerId"));

        TableColumn<TankerViewModel, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<TankerViewModel, Boolean> readyColumn = new TableColumn<>("Ready to Serve");
        readyColumn.setCellValueFactory(new PropertyValueFactory<>("readyToServe"));

        tableView.getColumns().addAll(idColumn, nameColumn, readyColumn);
        tableView.setItems(tankerData);

        return tableView;
    }

    private TableView<HouseOrderViewModel> createHouseOrderTableView() {
        TableView<HouseOrderViewModel> tableView = new TableView<>();

        TableColumn<HouseOrderViewModel, String> houseColumn = new TableColumn<>("House");
        houseColumn.setCellValueFactory(new PropertyValueFactory<>("houseName"));

        TableColumn<HouseOrderViewModel, String> timeColumn = new TableColumn<>("Order Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("orderTime"));

        TableColumn<HouseOrderViewModel, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(houseColumn, timeColumn, statusColumn);
        tableView.setItems(houseOrderData);

        return tableView;
    }

    private void startUIUpdates() {
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000); // Update every 2 seconds

                    Platform.runLater(() -> {
                        refreshTankerTable();
                        refreshHouseOrderTable();
                    });

                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void refreshTankerTable() {
        try {
            tankerData.clear();
            for (Map.Entry<Integer, TankerDetails> entry : office.getTankers().entrySet()) {
                TankerDetails details = entry.getValue();
                tankerData.add(new TankerViewModel(
                        entry.getKey(),
                        "Tanker " + entry.getKey(),
                        details.isReady()
                ));
            }
        } catch (Exception e) {
            showAlert("Error refreshing tanker table: " + e.getMessage());
        }
    }

    private void refreshHouseOrderTable() {
        try {
            houseOrderData.clear();

            for (OrderRequest order : office.getPendingOrders()) {
                String houseName = order.getHost() + ":" + order.getPort();
                LocalDateTime orderTime = pendingHouseOrders.getOrDefault(houseName, LocalDateTime.now());
                pendingHouseOrders.put(houseName, orderTime);

                houseOrderData.add(new HouseOrderViewModel(
                        houseName,
                        orderTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "Pending"
                ));
            }
        } catch (Exception e) {
            showAlert("Error refreshing house order table: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Office Management");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
