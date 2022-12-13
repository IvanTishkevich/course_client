module com.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens com.mx.application to javafx.fxml;
    opens com.mx.logic.controllers to javafx.fxml;
    opens com.mx.data to javafx.base;

    exports com.mx.application;
    exports com.mx.logic.controllers;
}
