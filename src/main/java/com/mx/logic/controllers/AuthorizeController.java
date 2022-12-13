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

import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

public class AuthorizeController {
    private Client client = Client.getInstance();

    @FXML
    private TextField loginField;

    @FXML
    private Button signInBtn, regBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane alert;

    @FXML
    private Text text;

    @FXML
    void handlerAlert(MouseEvent event) {
        Alerts.timelineStop();
    }

    @FXML
    private void initialize() {
        Alerts.setAlert(alert, text);
        regBtn.setOnAction(e -> regWindow());
        signInBtn.setOnAction(e -> signIn());

        loginField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[A-z]|[0-9]+")));
        passwordField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[^ ]")));
    }


    private void signIn() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (!login.equals("") && !password.equals("")) {
            Account user = new Account(login, password);
            client.writeObject("authorize", user);
            if (client.inputBooleanUser()) {
                if (client.getUser().getRole().equals("banned")) {
                    setEffectField(new InnerShadow(7, Color.RED));
                    Alerts.alertShow("Ваш аккаунт заблокирован!");
                    client.clearUser();
                    return;
                }
                setEffectField(null);
                clearAllFiled();

                MainApplication.stage.setScene(new Scene(loadScene("main.fxml")));
            } else {
                setEffectField(new InnerShadow(7, Color.RED));
                Alerts.alertShow("Неправильный логин или пароль");
            }
        } else {
            Alerts.alertShow("Введите логин и пароль");
        }
    }

    private void regWindow() {
        Alerts.timelineStop();
        setEffectField(null);
        clearAllFiled();

        MainApplication.stage.setScene(new Scene(loadScene("registration.fxml")));
    }

    private void setEffectField(Effect effect) {
        loginField.setEffect(effect);
        passwordField.setEffect(effect);
    }

    private void clearAllFiled() {
        loginField.clear();
        passwordField.clear();
    }

    private Parent loadScene(String file) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/mx/application/" + file));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
