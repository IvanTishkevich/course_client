package com.mx.logic.controllers;

import com.mx.logic.controllers.interfaces.Observable;
import com.mx.logic.controllers.interfaces.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class AlertController implements Observable {
    private static List<Observer> observers = new LinkedList<>();
    private static String textAlert;
    private static String textButton;

    @FXML
    private Button acceptBtn;

    @FXML
    private Label text;

    @FXML
    void acceptAlert(ActionEvent event) {
        notifyObservers(textButton);
        closeStage();
    }

    @FXML
    void closeAlert(ActionEvent event) {
        notifyObservers("Отмена");
        closeStage();
    }

    @FXML
    private void initialize() {
        text.setText(textAlert);
        acceptBtn.setText(textButton);
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
        System.out.println("Всех подписчиков: " + observers.size());
    }

    @Override
    public void removeObserver() {
        if(observers.size() >= 2){
            observers.remove(1);
        }
    }

    @Override
    public void removeAllObserver() {
        observers.clear();
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.notification(message);
        }
    }

    public void showAlert(String textAl, String textBtn) {
        textAlert = textAl;
        textButton = textBtn;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mx/application/alert.fxml"));

        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        alert.show();
    }

    private void closeStage(){
        Stage stage = (Stage) acceptBtn.getScene().getWindow();
        stage.close();
    }
}