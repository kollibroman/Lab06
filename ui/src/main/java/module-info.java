module org.filip.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.rmi;
    requires logic;
    requires static lombok;

    opens org.filip.ui to javafx.fxml;
    exports org.filip.ui;
}