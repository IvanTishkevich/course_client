package com.mx.logic.controllers;

import com.mx.application.MainApplication;
import com.mx.logic.client.Client;

import com.mx.data.Account;
import com.mx.tools.Alerts;
import com.mx.tools.Checks;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


import java.io.IOException;
import java.util.ArrayList;


public class RegisterController {
    private Client client = Client.getInstance();

    @FXML
    private TextField loginField;

    @FXML
    private Button regBtn, backBtn;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private TextField nameField, surnameField, addressField, telephoneField, emailField;

    @FXML
    private Label logWarning, telephoneWarning, passwordOK, passwordWarning;

    @FXML
    private AnchorPane alert;

    @FXML
    private Label passwordError;

    @FXML
    private Text text;

    private ArrayList<String> arrayLogin = new ArrayList<>();

    @FXML
    void handlerAlert(MouseEvent event) {
        Alerts.timelineStop();
    }

    @FXML
    private void initialize() {
        Alerts.setAlert(alert, text);
        backBtn.setOnAction(e -> authorizeWindow());
        regBtn.setOnAction(e -> registration());
        loginField.addEventFilter(KeyEvent.ANY, e -> loginEvent());
        confirmPasswordField.addEventFilter(KeyEvent.ANY, e -> passwordEvent());
        passwordField.addEventFilter(KeyEvent.ANY, e -> passwordEvent());
        setFormatterText();
    }

    private void setFormatterText() {
        loginField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[A-z]|[0-9]+")));
        passwordField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[^ ]")));
        confirmPasswordField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[^ ]")));
        nameField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        surnameField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        addressField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[0-9]+|[-,. ]")));
        telephoneField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void registration() {
        confirmPasswordField.setEffect(null);
        telephoneWarning.setVisible(false);

        String login = loginField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        String address = addressField.getText();
        String telephone = telephoneField.getText();


        if (!login.equals("") && !email.equals("") && !password.equals("")
                && !name.equals("") && !surname.equals("")
                && !address.equals("") && !telephone.equals("")) {

            if (password.length() < 6) {
                return;
            }
            if (telephone.length() < 9) {
                telephoneWarning.setVisible(true);
                return;
            }
            if (!password.equals(confirmPassword)) {
                return;
            }

            Account user = new Account(login, email, password, name, surname, address, telephone);
            client.writeObject("reg", user);
            if (client.inputBoolean()) {
                clearAllField();
                Alerts.alertShow("Регистрация прошла успешно. Авторизуйтесь.");
            } else {
                arrayLogin.add(login);
                loginField.setEffect(new InnerShadow(7, Color.RED));
                logWarning.setVisible(true);
            }

        } else {
            Alerts.alertShow("Заполните все поля");
        }
    }

    private void clearAllField() {
        confirmPasswordField.setEffect(null);
        loginField.setEffect(null);

        logWarning.setVisible(false);
        passwordWarning.setVisible(false);
        passwordOK.setVisible(false);

        loginField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        nameField.clear();
        surnameField.clear();
        addressField.clear();
        telephoneField.clear();
    }

    private void authorizeWindow() {
        Alerts.timelineStop();
        MainApplication.stage.setScene(new Scene(loadScene("authorization.fxml")));
    }

    private void loginEvent() {
        for (String log : arrayLogin) {
            if (loginField.getText().equals(log)) {
                loginField.setEffect(new InnerShadow(7, Color.RED));
                logWarning.setVisible(true);
                break;
            } else {
                loginField.setEffect(null);
                logWarning.setVisible(false);
            }
        }
    }

    private void passwordEvent() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.equals("") && confirmPassword.equals("")) {
            confirmPasswordField.setEffect(null);
            passwordWarning.setVisible(false);
            passwordError.setVisible(false);
            return;
        }
        if (password.length() < 6) {
            passwordError.setVisible(true);
        } else {
            passwordError.setVisible(false);
        }

        if (password.equals(confirmPassword)) {
            confirmPasswordField.setEffect(null);
            passwordOK.setVisible(true);
            passwordWarning.setVisible(false);
        } else {
            confirmPasswordField.setEffect(new InnerShadow(7, Color.RED));
            passwordOK.setVisible(false);
            passwordWarning.setVisible(true);
        }
    }

    private Parent loadScene(String file)  {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mx/application/"+file));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}