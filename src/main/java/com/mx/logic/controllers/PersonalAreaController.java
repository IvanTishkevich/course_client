package com.mx.logic.controllers;

import com.mx.logic.client.Client;
import com.mx.data.Account;
import com.mx.tools.Checks;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import java.util.Arrays;

public class PersonalAreaController {
    private Client client;
    @FXML
    private TextField nameField, surnameField, addressField, telephoneField, emailField;

    @FXML
    private Label labelLoginPane, labelRolePane;

    @FXML
    private Button editInfoBtn;

    String oldEmail, oldName, oldSurname, oldAddress, oldTelephone;

    @FXML
    private void editInfo() {
        boolean flag;
        if (nameField.isEditable()) {
            String email = emailField.getText();
            String name = nameField.getText();
            String surname = surnameField.getText();
            String address = addressField.getText();
            String telephone = telephoneField.getText();

            if (!(email.equals(oldEmail) && name.equals(oldName) && surname.equals(oldSurname) &&
                    address.equals(oldAddress) && telephone.equals(oldTelephone))) {
                client.getUser().setInfo(email, name, surname, address, telephone);


                Account user = new Account(client.getUser().getLogin(), email, name, surname, address, telephone);
                client.writeObject("editPersInfo", user);
            }
            flag = false;
            editInfoBtn.setText("Изменить данные");
        } else {
            oldEmail = emailField.getText();
            oldName = nameField.getText();
            oldSurname = surnameField.getText();
            oldAddress = addressField.getText();
            oldTelephone = telephoneField.getText();
            editInfoBtn.setText("Сохранить");
            flag = true;
        }
        for (TextField field : Arrays.asList(nameField, surnameField, emailField, addressField, telephoneField)) {
            field.setEditable(flag);
        }
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();

        setInfoUser();
        setLabelPersPane();
        setFormatterText();
    }

    private void setFormatterText() {
        nameField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        surnameField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        addressField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[0-9]+|[-,. ]")));
        telephoneField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void setInfoUser() {
        Account user = client.getUser();
        nameField.setText(user.getName());
        surnameField.setText(user.getSurname());
        emailField.setText(user.getEmail());
        addressField.setText(user.getAddress());
        telephoneField.setText(user.getTelephone());
    }

    private void setLabelPersPane() {
        labelLoginPane.setText(client.getUser().getLogin());
        String roleUser = client.getUser().getRole();
        labelRolePane.setText(client.getUser().getName());
        if (roleUser.equals("user")) {
            labelRolePane.setText("Пользователь");
        } else if (roleUser.equals("admin")) {
            labelRolePane.setText("Администратор");
        } else {
            labelRolePane.setText("Главный администратор");
        }
    }
}
