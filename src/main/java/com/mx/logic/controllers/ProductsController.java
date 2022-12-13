package com.mx.logic.controllers;

import com.mx.logic.client.Client;
import com.mx.logic.controllers.interfaces.Observer;
import com.mx.data.Selling;
import com.mx.data.Technique;
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

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class ProductsController implements Observer {
    private Client client;
    private AlertController alertController;
    private Technique selectedTech;

    @FXML
    private AnchorPane productsPane, addPane, editPane, salePane;

    @FXML
    private TableView<Technique> techTable;

    @FXML
    private TextField filterField;

    @FXML
    private TableColumn<Technique, String> colTypeName, colBrand, colModel;

    @FXML
    private TableColumn<Technique, Integer> colYear, colQuantity;

    @FXML
    private Button refreshBtn, delBtn, regBtn;

    @FXML
    private TextField typeNameAddField, modelAddField, releaseYearAddField, quantityInStockAddField, brandAddField;

    @FXML
    private Label yearWarningAdd, quantityWarningAdd, fieldWarningAdd;

    @FXML
    private TextField typeNameEditField, modelEditField, releaseYearEditField, quantityInStockEditField, brandEditField;

    @FXML
    private TextField priceSaleField, quantitySaleField;

    @FXML
    private Button closeAddPaneBtn, closeEditPaneBtn;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label yearWarningEdit, fieldWarningEdit;

    @FXML
    private Label techSaleLabel, quantityWarningSale, fieldWarningSale,priceWarningSale;

    @FXML
    private Label successAdd, successEdit,successSale;


    private ObservableList<Technique> techData = FXCollections.observableArrayList();


    @FXML
    void getTech(MouseEvent event) {
        selectedTech = techTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    void refreshTable(ActionEvent event) {
        selectedTech = null;
        client.writeObject("showTech");
        ArrayList<Technique> list = client.inputList();
        techData.clear();
        techData.addAll(list);
        techTable.setItems(techData);
        filtering();
    }

    @FXML
    void addTechBtn(ActionEvent event) {
        addPane.setVisible(true);
        productsPane.setEffect(new BoxBlur());
        productsPane.setDisable(true);
    }

    @FXML
    void delTech(ActionEvent event) {
        if (selectedTech != null) {
            alertController.showAlert("Удалить выбранную технику?", "Удалить технику");
            productsPane.setEffect(new BoxBlur());
        }
    }

    @FXML
    void addTech(ActionEvent event) {
        String typeName = typeNameAddField.getText();
        String brand = brandAddField.getText();
        String model = modelAddField.getText();
        String releaseYear = releaseYearAddField.getText();
        String quantityInStock = quantityInStockAddField.getText();

        if (!typeName.equals("") && !brand.equals("") && !model.equals("")
                && !releaseYear.equals("") && !quantityInStock.equals("")) {
            if(Integer.parseInt(releaseYear) > LocalDate.now().getYear() || Integer.parseInt(releaseYear)<LocalDate.now().getYear()-20){
                Alerts.showWarning(yearWarningAdd);
                return;
            }
            Technique tech = new Technique(typeName, brand, model, Integer.parseInt(releaseYear), Integer.parseInt(quantityInStock));
            client.writeObject("addTech", tech);
            client.writeObject(client.getUser().getUserID());
            Alerts.showWarning(successAdd);
            clearAddAllField();
        } else {
            Alerts.showWarning(fieldWarningAdd);
        }
    }

    @FXML
    void editTechBtn(ActionEvent event) {
        if (selectedTech != null) {
            typeNameEditField.setText(selectedTech.getTypeName());
            brandEditField.setText(selectedTech.getBrand());
            modelEditField.setText(selectedTech.getModel());
            releaseYearEditField.setText(String.valueOf(selectedTech.getReleaseYear()));
            quantityInStockEditField.setText(String.valueOf(selectedTech.getQuantityInStock()));

            editPane.setVisible(true);
            productsPane.setEffect(new BoxBlur());
            productsPane.setDisable(true);
            setTexFormatterEdit();
        }
    }

    @FXML
    void editTech(ActionEvent event) {
        String typeName = typeNameEditField.getText();
        String brand = brandEditField.getText();
        String model = modelEditField.getText();
        String releaseYear = releaseYearEditField.getText();
        String quantityInStock = quantityInStockEditField.getText();

        if (!typeName.equals("") && !brand.equals("") && !model.equals("")
                && !releaseYear.equals("") && !quantityInStock.equals("")) {

            if(Integer.parseInt(releaseYear) > LocalDate.now().getYear() || Integer.parseInt(releaseYear)<LocalDate.now().getYear()-20){
                Alerts.showWarning(yearWarningEdit);
                return;
            }

            Technique tech = new Technique(selectedTech.getTechID(), typeName, brand, model, Integer.parseInt(releaseYear), Integer.parseInt(quantityInStock));
            client.writeObject("editTech", tech);
            Alerts.showWarning(successEdit);
        } else {
            Alerts.showWarning(fieldWarningEdit);
        }
    }

    @FXML
    void addSaleBtn(ActionEvent event) {
        if (selectedTech != null) {
            techSaleLabel.setText(selectedTech.getBrand() + " " + selectedTech.getModel());
            datePicker.setValue(LocalDate.now());
            salePane.setVisible(true);
            productsPane.setEffect(new BoxBlur());
            productsPane.setDisable(true);
        }
    }

    @FXML
    void saleTech(ActionEvent event) {
        String price = priceSaleField.getText();
        String quantity = quantitySaleField.getText();

        if (!price.equals("") && !quantity.equals("")) {

            if (Integer.parseInt(quantity) > selectedTech.getQuantityInStock() || quantity.equals("0")) {
                Alerts.showWarning(quantityWarningSale);
                return;
            }
            if(price.equals("0")){
                Alerts.showWarning(priceWarningSale);
                return;
            }
            selectedTech.setQuantityInStock(selectedTech.getQuantityInStock()-Integer.parseInt(quantity));

            Technique tech = new Technique(selectedTech.getTechID(),selectedTech.getQuantityInStock());
            Selling selling = new Selling(Integer.parseInt(price), Integer.parseInt(quantity), Date.valueOf(datePicker.getValue()), tech);
            client.writeObject("addSelling", selling);
            Alerts.showWarning(successSale);
            clearSaleAllField();
        } else {
            Alerts.showWarning(fieldWarningSale);
        }
    }

    @FXML
    void closePane(ActionEvent event) {
        Object object = event.getSource();
        if (object == closeAddPaneBtn) {
            addPane.setVisible(false);
            clearAddAllField();
        } else if (object == closeEditPaneBtn) {
            editPane.setVisible(false);
        } else {
            salePane.setVisible(false);
            clearSaleAllField();
        }
        productsPane.setEffect(null);
        productsPane.setDisable(false);
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();
        alertController = new AlertController();
        alertController.registerObserver(this);
        initColumn();
        initDataPicker();
        setTextFormatterAdd();
        setTextFormatterSale();
    }

    private void initColumn() {
        colTypeName.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));
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

    @Override
    public void notification(String message) {
        if (message.equals("Удалить технику")) {
            client.writeObject("delTech", selectedTech);
            techData.remove(selectedTech);
        }
        productsPane.setEffect(null);
    }

    private void filtering() {
        FilteredList<Technique> filteredData = new FilteredList<>(techData, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(tech -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (tech.getBrand().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (tech.getModel().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Technique> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(techTable.comparatorProperty());
        techTable.setItems(sortedData);
    }

    private void setTexFormatterEdit() {
        typeNameEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        releaseYearEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
        quantityInStockEditField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void setTextFormatterAdd() {
        typeNameAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 46, "[А-я]|[- ]")));
        releaseYearAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
        quantityInStockAddField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void setTextFormatterSale() {
        priceSaleField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
        quantitySaleField.setTextFormatter(new TextFormatter<String>(change -> Checks.Formatter(change, 10, "[0-9]+")));
    }

    private void clearAddAllField() {
        typeNameAddField.clear();
        brandAddField.clear();
        modelAddField.clear();
        releaseYearAddField.clear();
        quantityInStockAddField.clear();
    }

    private void clearSaleAllField() {
        priceSaleField.clear();
        quantitySaleField.clear();
    }
}
