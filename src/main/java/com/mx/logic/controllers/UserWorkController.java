package com.mx.logic.controllers;

import com.mx.logic.client.Client;
import com.mx.logic.controllers.interfaces.Observer;
import com.mx.data.Account;
import com.mx.tools.Alerts;
import com.mx.tools.Checks;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class UserWorkController implements Observer {
    private Client client;
    private Account selectedUser;
    private AlertController alertController;

    @FXML
    private TableView<Account> usersTable;

    @FXML
    private TableColumn<Account, String> colLogin, colEmail, colRole, colName, colSurname, colAddress, colTelephone;

    @FXML
    private TextField filterField;

    @FXML
    private AnchorPane regPane, rolePane, userWorkPane;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField, nameField, surnameField, addressField, telephoneField, emailField;

    @FXML
    private Label logWarning, telephoneWarning, passwordError, fieldWarning, labelLogin, success;

    @FXML
    private Button closeRegPaneBtn, editBtn;

    private ObservableList<Account> usersData = FXCollections.observableArrayList();

    @FXML
    void addUser(ActionEvent event) {
        regPane.setVisible(true);
        userWorkPane.setEffect(new BoxBlur());
        userWorkPane.setDisable(true);
    }

    @FXML
    void closePane(ActionEvent event) {
        Object object = event.getSource();
        if (object == closeRegPaneBtn) {
            regPane.setVisible(false);
            clearAllField();
        } else {
            rolePane.setVisible(false);
        }
        userWorkPane.setEffect(null);
        userWorkPane.setDisable(false);
    }

    @FXML
    void regUser(ActionEvent event) {
        loginField.setEffect(null);

        String login = loginField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = "user";
        String name = nameField.getText();
        String surname = surnameField.getText();
        String address = addressField.getText();
        String telephone = telephoneField.getText();

        if (!login.equals("") && !email.equals("") && !password.equals("")
                && !name.equals("") && !surname.equals("")
                && !address.equals("") && !telephone.equals("")) {

            if (password.length() < 6) {
                Alerts.showWarning(passwordError);
                return;
            }
            if (telephone.length() < 9) {
                Alerts.showWarning(telephoneWarning);
                return;
            }

            Account user = new Account(login, email, password, role, name, surname, address, telephone);
            client.writeObject("reg", user);
            if (client.inputBoolean()) {
                Alerts.showWarning(success);
                clearAllField();
            } else {
                loginField.setEffect(new InnerShadow(7, Color.RED));
                Alerts.showWarning(logWarning);
            }
        } else {
            Alerts.showWarning(fieldWarning);
        }
    }

    @FXML
    void delUser(ActionEvent event) {
        if (selectedUser != null) {
            alertController.showAlert("Удалить пользователя?", "Удалить аккаунт");
            userWorkPane.setEffect(new BoxBlur());
        }
    }

    @FXML
    void refreshTable(ActionEvent event) {
        selectedUser = null;
        usersData.clear();
        client.writeObject("showUsers");
        ArrayList<Account> list = client.inputList();
        usersData.addAll(list);
        usersTable.setItems(usersData);
        filtering();
    }

    @FXML
    void banUser(ActionEvent event) {
        if (selectedUser != null) {
            if (selectedUser.getRole().equals("user")) {
                alertController.showAlert("Заблокировать пользователя?", "Заблокировать");
                userWorkPane.setEffect(new BoxBlur());
            } else {
                alertController.showAlert("Разблокировать пользователя?", "Разблокировать");
                userWorkPane.setEffect(new BoxBlur());
            }
        }
    }

    @FXML
    void getUser(MouseEvent event) {
        selectedUser = usersTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();
        alertController = new AlertController();
        alertController.registerObserver(this);
        initColumn();
        setTextFormatter();
    }

    private void setTextFormatter() {
        loginField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[A-z]|[0-9]+")));
        passwordField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[^ ]")));
        nameField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        surnameField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        addressField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[0-9]+|[-,. ]")));
        telephoneField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void filtering() {
        FilteredList<Account> filteredData = new FilteredList<>(usersData, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getSurname().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Account> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(usersTable.comparatorProperty());
        usersTable.setItems(sortedData);
    }

    private void initColumn() {
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    }

    private void clearAllField() {
        loginField.clear();
        emailField.clear();
        passwordField.clear();
        nameField.clear();
        surnameField.clear();
        addressField.clear();
        telephoneField.clear();
    }

    @Override
    public void notification(String message) {
        if (message.equals("Удалить аккаунт")) {
            client.writeObject("delUser", selectedUser);
            usersData.remove(selectedUser);
        } else if (message.equals("Заблокировать")) {
            selectedUser.setRole("banned");
            client.writeObject("editRole", selectedUser);
        }else if(message.equals("Разблокировать")){
            selectedUser.setRole("user");
            client.writeObject("editRole", selectedUser);
        }
        userWorkPane.setEffect(null);
    }
}
