package com.mx.logic.controllers;

import com.mx.logic.client.Client;
import com.mx.logic.controllers.interfaces.Observer;
import com.mx.data.Selling;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class SalesController implements Observer {
    private Client client;
    private AlertController alertController;
    private Selling selectedSelling;

    @FXML
    private AnchorPane salesPane, editPane;

    @FXML
    private TableView<Selling> sellingTable;

    @FXML
    private TableColumn<Selling, String> colBrand, colModel;

    @FXML
    private TableColumn<Selling, Integer> colPrice, colQuantity;

    @FXML
    private TableColumn<String, Date> colDate;

    @FXML
    private Button refreshBtn, delBtn, editBtn, closeEditPaneBtn;

    @FXML
    private TextField filterField, priceEditField, editTechBtn;

    @FXML
    private Label priceWarningEdit, techSaleLabel, fieldWarningEdit,successSale;

    @FXML
    private DatePicker datePicker;

    private ObservableList<Selling> sellingData = FXCollections.observableArrayList();

    @FXML
    void getSelling(MouseEvent event) {
        selectedSelling = sellingTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    void refreshTable(ActionEvent event) {
        selectedSelling = null;
        client.writeObject("showSelling");
        ArrayList<Selling> list = client.inputList();
        sellingData.clear();
        sellingData.addAll(list);
        sellingTable.setItems(sellingData);
        filtering();
    }

    @FXML
    void delSelling(ActionEvent event) {
        if (selectedSelling != null) {
            alertController.showAlert("Отменить выбранную продажу?", "Отменить продажу");
            salesPane.setEffect(new BoxBlur());
        }
    }

    @FXML
    void editSellingBtn(ActionEvent event) {
        if (selectedSelling != null) {
            techSaleLabel.setText(selectedSelling.getTechnique().getBrand() + " " + selectedSelling.getTechnique().getModel());
            priceEditField.setText(String.valueOf(selectedSelling.getSalePrice()));
            datePicker.setValue(selectedSelling.getSaleDate().toLocalDate());

            editPane.setVisible(true);
            salesPane.setEffect(new BoxBlur());
            salesPane.setDisable(true);
            setTexFormatterEdit();
        }
    }

    @FXML
    void editSelling(ActionEvent event) {
        String price = priceEditField.getText();

        if (!price.equals("")) {
            if(price.equals("0")){
                Alerts.showWarning(priceWarningEdit);
                return;
            }
            Selling selling = new Selling(selectedSelling.getSellingID(), Integer.parseInt(price), Date.valueOf(datePicker.getValue()));
            client.writeObject("editSelling", selling);
            Alerts.showWarning(successSale);
        } else {
            Alerts.showWarning(fieldWarningEdit);
        }
    }

    @FXML
    void closePane(ActionEvent event) {
        fieldWarningEdit.setVisible(false);

        editPane.setVisible(false);
        salesPane.setEffect(null);
        salesPane.setDisable(false);
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();
        alertController = new AlertController();
        alertController.registerObserver(this);
        initDataPicker();
        initColumn();
    }

    private void initDataPicker() {
        datePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) > 0);
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
        colPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
    }

    private void setTexFormatterEdit() {
        priceEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void filtering() {
        FilteredList<Selling> filteredData = new FilteredList<>(sellingData, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(selling -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (selling.getTechnique().getBrand().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (selling.getTechnique().getModel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Selling> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(sellingTable.comparatorProperty());
        sellingTable.setItems(sortedData);
    }

    @Override
    public void notification(String message) {
        if (message.equals("Отменить продажу")) {
            client.writeObject("delSelling", selectedSelling);
            sellingData.remove(selectedSelling);
        }
        salesPane.setEffect(null);

    }
}
