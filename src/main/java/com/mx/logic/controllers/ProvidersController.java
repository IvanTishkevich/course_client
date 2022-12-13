package com.mx.logic.controllers;

import com.mx.logic.client.Client;
import com.mx.logic.controllers.interfaces.Observer;
import com.mx.data.Providers;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class ProvidersController implements Observer {
    private Client client;
    private AlertController alertController;
    private Providers selectedProvider;

    @FXML
    private AnchorPane providersPane, addPane, editPane;

    @FXML
    private TableView<Providers> providersTable;

    @FXML
    private TableColumn<Providers, String> colName, colAddress, colTelephone;

    @FXML
    private TextField filterField;

    @FXML
    private TextField nameAddField, telephoneAddField, addressAddField;

    @FXML
    private Button closeAddPaneBtn;

    @FXML
    private Label telephoneWarningAdd, fieldWarningAdd;

    @FXML
    private TextField nameEditField, telephoneEditField, addressEditField;

    @FXML
    private Label telephoneWarningEdit, fieldWarningEdit;

    @FXML
    private Label successAdd,successEdit;

    private ObservableList<Providers> providersData = FXCollections.observableArrayList();

    @FXML
    void getProvider(MouseEvent event) {
        selectedProvider = providersTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    void refreshTable(ActionEvent event) {
        selectedProvider = null;
        client.writeObject("showProviders");
        ArrayList<Providers> list = client.inputList();
        providersData.clear();
        providersData.addAll(list);
        providersTable.setItems(providersData);
        filtering();
    }

    @FXML
    void addProviderBtn(ActionEvent event) {
        addPane.setVisible(true);
        providersPane.setEffect(new BoxBlur());
        providersPane.setDisable(true);
    }

    @FXML
    void addProvider(ActionEvent event) {
        String name = nameAddField.getText();
        String address = addressAddField.getText();
        String telephone = telephoneAddField.getText();

        if (!name.equals("") && !address.equals("") && !telephone.equals("")) {
            if (telephone.length() < 9) {
                Alerts.showWarning(telephoneWarningAdd);
                return;
            }
            Providers provider = new Providers(name, address, telephone);
            client.writeObject("addProvider", provider);
            Alerts.showWarning(successAdd);
            clearAddAllField();
        } else {
            Alerts.showWarning(fieldWarningAdd);
        }
    }

    @FXML
    void editProviderBtn(ActionEvent event) {
        if (selectedProvider != null) {
            nameEditField.setText(selectedProvider.getName());
            addressEditField.setText(selectedProvider.getAddress());
            telephoneEditField.setText(selectedProvider.getTelephone());

            editPane.setVisible(true);
            providersPane.setEffect(new BoxBlur());
            providersPane.setDisable(true);
            setTexFormatterEdit();
        }
    }

    @FXML
    void editProvider(ActionEvent event) {
        String name = nameEditField.getText();
        String address = addressEditField.getText();
        String telephone = telephoneEditField.getText();

        if (!name.equals("") && !address.equals("") && !telephone.equals("")) {
            if (telephone.length() < 9) {
                Alerts.showWarning(telephoneWarningEdit);
                return;
            }

            Providers provider = new Providers(selectedProvider.getProviderID(), name, address, telephone);
            client.writeObject("editProvider", provider);
            Alerts.showWarning(successEdit);
        } else {
            Alerts.showWarning(fieldWarningEdit);
        }
    }

    @FXML
    void delProvider(ActionEvent event) {
        if (selectedProvider != null) {
            alertController.showAlert("Удалить выбранного поставщика?", "Удалить поставщика");
            providersPane.setEffect(new BoxBlur());
        }
    }

    @FXML
    void closePane(ActionEvent event) {
        Object object = event.getSource();
        if (object == closeAddPaneBtn) {
            addPane.setVisible(false);
            clearAddAllField();
        } else {
            editPane.setVisible(false);
        }
        providersPane.setEffect(null);
        providersPane.setDisable(false);
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();
        alertController = new AlertController();
        alertController.registerObserver(this);
        initColumn();
        setTextFormatterAdd();
    }

    private void setTexFormatterEdit(){
        nameEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        addressEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[0-9]+|[-,. ]")));
        telephoneEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void setTextFormatterAdd() {
        nameAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        addressAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[0-9]+|[-,. ]")));
        telephoneAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void initColumn() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    }

    @Override
    public void notification(String message) {
        if (message.equals("Удалить поставщика")) {
            client.writeObject("delProvider", selectedProvider);
            providersData.remove(selectedProvider);
        }
        providersPane.setEffect(null);
    }

    private void filtering() {
        FilteredList<Providers> filteredData = new FilteredList<>(providersData, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(provider -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (provider.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Providers> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(providersTable.comparatorProperty());
        providersTable.setItems(sortedData);
    }

    private void clearAddAllField() {
        nameAddField.clear();
        addressAddField.clear();
        telephoneAddField.clear();
    }
}
