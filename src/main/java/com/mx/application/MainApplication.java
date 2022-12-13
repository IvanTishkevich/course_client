package com.mx.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        MainApplication.stage = stage;
        stage.setTitle("Учёт продаж");
        stage.setScene(new Scene(loadScene("authorization.fxml")));
        stage.setResizable(false);
        stage.show();
    }

    private Parent loadScene(String nameFile) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(nameFile));
            return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
