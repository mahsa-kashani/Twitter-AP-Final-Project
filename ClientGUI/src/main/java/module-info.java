module com.clientgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.clientgui to javafx.fxml;
    exports com.clientgui;
}