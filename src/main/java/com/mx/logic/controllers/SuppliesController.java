package com.mx.logic.controllers;

import com.mx.logic.client.Client;
import com.mx.logic.controllers.interfaces.Observer;
import com.mx.data.Providers;
import com.mx.data.Supplies;
import com.mx.data.Technique;
import com.mx.tools.Alerts;
import com.mx.tools.Checks;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.util.Callback;

//import javax.security.auth.callback.Callback;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;

public class SuppliesController implements Observer {
    private Client client;
    private AlertController alertController;
    private Supplies selectedSupply;
    @FXML
    private AnchorPane suppliesPane, editPane, addPane;

    @FXML
    private TableView<Supplies> suppliesTable;

    @FXML
    private TableColumn<Supplies, String> colBrand, colModel;

    @FXML
    private TableColumn<Supplies, Date> colDate;

    @FXML
    private TableColumn<Supplies, Integer> colPrice, colQuantity;

    @FXML
    private TableColumn<Supplies, String> colProvider, colStatus;

    @FXML
    private Button refreshBtn, delBtn, addBtn, closeEditPaneBtn;

    @FXML
    private TextField filterField;

    @FXML
    private TextField quantityEditField, priceEditField;

    @FXML
    private Label priceWarningEdit, quantityWarningEdit, fieldWarningEdit, quantityWarningAdd, priceWarningAdd, fieldWarningAdd;

    @FXML
    private DatePicker datePicker, datePickerAdd;

    @FXML
    private TextField typeNameAddField, modelAddField, brandAddField, quantityAddField, priceAddField;

    @FXML
    private ComboBox<Providers> providerBox;

    @FXML
    private Label successAdd, successEdit;

    private ObservableList<Supplies> supplyData = FXCollections.observableArrayList();

    @FXML
    void acceptSupply(ActionEvent event) {
        if (selectedSupply != null && !selectedSupply.getStatus().equals("Принято")) {
            alertController.showAlert("Принять выбранную поставку?", "Принять поставку");
            suppliesPane.setEffect(new BoxBlur());
        }
    }

    @FXML
    void refreshTable(ActionEvent event) {
        selectedSupply = null;
        client.writeObject("showSupplies");
        ArrayList<Supplies> list = client.inputList();
        supplyData.clear();
        supplyData.addAll(list);
        suppliesTable.setItems(supplyData);
        filtering();
    }

    @FXML
    void addSupplyBtn(ActionEvent event) {
        client.writeObject("showProviders");
        ArrayList<Providers> list = client.inputList();
        ObservableList<Providers> providers = FXCollections.observableArrayList();
        providers.setAll(list);
        providerBox.setItems(providers);
        providerBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Providers> call(ListView<Providers> p) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Providers item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        }
                    }
                };
            }
        });

        datePickerAdd.setValue(LocalDate.now());
        addPane.setVisible(true);
        suppliesPane.setEffect(new BoxBlur());
        suppliesPane.setDisable(true);
    }

    @FXML
    void addSupply(ActionEvent event) {
        String typeName = typeNameAddField.getText();
        String brand = brandAddField.getText();
        String model = modelAddField.getText();
        String price = priceAddField.getText();
        String quantity = quantityAddField.getText();
        String providerName;
        if (providerBox.getValue() == null) {
            providerName = "";
        } else {
            providerName = providerBox.getValue().getName();
        }

        if (!typeName.equals("") && !brand.equals("") && !model.equals("") &&
                !price.equals("") && !quantity.equals("") && !providerName.equals("")) {

            if (price.equals("0")) {
                Alerts.showWarning(priceWarningAdd);
                return;
            }
            if (quantity.equals("0")) {
                Alerts.showWarning(quantityWarningAdd);
                return;
            }

            Technique technique = new Technique(typeName, brand, model);
            Providers provider = new Providers(providerBox.getValue().getProviderID(), providerName);
            Supplies supply = new Supplies(Date.valueOf(datePickerAdd.getValue()),
                    Integer.parseInt(price), Integer.parseInt(quantity), technique, provider);

            client.writeObject("addSupply", supply);
            client.writeObject(client.getUser().getUserID());
            Alerts.showWarning(successAdd);
            clearAddAllField();
        } else {
            Alerts.showWarning(fieldWarningAdd);
        }
    }

    @FXML
    void editTechBtn(ActionEvent event) {
        if (selectedSupply != null && !selectedSupply.getStatus().equals("Принято")) {
            datePicker.setValue(selectedSupply.getSupplyDate().toLocalDate());
            priceEditField.setText(String.valueOf(selectedSupply.getPurchasePrice()));
            quantityEditField.setText(String.valueOf(selectedSupply.getQuantitySupplied()));

            editPane.setVisible(true);
            suppliesPane.setEffect(new BoxBlur());
            suppliesPane.setDisable(true);
        }
    }

    @FXML
    void editSupply(ActionEvent event) {
        fieldWarningEdit.setVisible(false);
        String price = priceEditField.getText();
        String quantity = quantityEditField.getText();

        if (!price.equals("") && !quantity.equals("")) {
            Supplies supply = new Supplies(selectedSupply.getSupplyID(), Date.valueOf(datePicker.getValue()), Integer.valueOf(price), Integer.valueOf(quantity));
            client.writeObject("editSupply", supply);
        } else {
            fieldWarningEdit.setVisible(true);
        }
    }

    @FXML
    void delSupply(ActionEvent event) {
        if (selectedSupply != null) {
            alertController.showAlert("Удалить выбранную поставку?", "Удалить поставку");
            suppliesPane.setEffect(new BoxBlur());
        }
    }

    @FXML
    void getSupply(MouseEvent event) {
        selectedSupply = suppliesTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    void closePane(ActionEvent event) {
        Object object = event.getSource();
        if (object == closeEditPaneBtn) {
            editPane.setVisible(false);
            fieldWarningEdit.setVisible(false);
            quantityWarningEdit.setVisible(false);
        } else {
            addPane.setVisible(false);
            clearAddAllField();
        }
        suppliesPane.setEffect(null);
        suppliesPane.setDisable(false);
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();
        alertController = new AlertController();
        alertController.registerObserver(this);
        initColumn();
        initDataPicker();
        setTexFormatterEdit();
        setTextFormatterAdd();
    }

    private void initDataPicker() {
        datePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });
        datePickerAdd.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });
    }

    private void initColumn() {
        colBrand.setCellValueFactory(data -> {
            StringProperty s = new SimpleStringProperty();
            s.setValue(data.getValue().getTechnique().getBrand());
            return s;
        });
        colModel.setCellValueFactory(data -> {
            StringProperty s = new SimpleStringProperty();
            s.setValue(data.getValue().getTechnique().getModel());
            return s;
        });
        colDate.setCellValueFactory(new PropertyValueFactory<>("supplyDate"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantitySupplied"));
        colProvider.setCellValueFactory(data -> {
            StringProperty s = new SimpleStringProperty();
            s.setValue(data.getValue().getProvider().getName());
            return s;
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setTexFormatterEdit() {
        quantityEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
        priceEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void setTextFormatterAdd() {
        typeNameAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        priceAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
        quantityAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void clearAddAllField() {
        fieldWarningAdd.setVisible(false);
        quantityWarningAdd.setVisible(false);

        typeNameAddField.clear();
        brandAddField.clear();
        modelAddField.clear();
        priceAddField.clear();
        quantityAddField.clear();
    }

    private void filtering() {
        FilteredList<Supplies> filteredData = new FilteredList<>(supplyData, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(supply -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (supply.getTechnique().getBrand().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (supply.getTechnique().getModel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Supplies> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(suppliesTable.comparatorProperty());
        suppliesTable.setItems(sortedData);
    }

    @Override
    public void notification(String message) {
        if (message.equals("Удалить поставку")) {
            client.writeObject("delSupply", selectedSupply);
            supplyData.remove(selectedSupply);
        } else if (message.equals("Принять поставку")) {
            selectedSupply.setStatus("Принято");
            client.writeObject("acceptSupply", selectedSupply);
        }
        suppliesPane.setEffect(null);
    }
}
