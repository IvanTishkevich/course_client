package com.mx.logic.controllers;

import com.mx.data.Technique;
import com.mx.logic.client.Client;
import com.mx.data.Providers;
import com.mx.data.Selling;
import com.mx.data.Supplies;
import com.mx.tools.Alerts;
import com.mx.tools.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class StatsController {
    private Client client;

    @FXML
    private AnchorPane statsPane;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ComboBox<String> monthBox;

    @FXML
    private TextField yearField;

    @FXML
    private Button showChartBtn;

    @FXML
    private ListView<String> resultList;

    @FXML
    private Label yearWarningChart, fieldWarningChart, resultProfit, sumSupply, successCreateFile;

    @FXML
    void showChart(ActionEvent event) {
        String year = yearField.getText();
        String month = monthBox.getValue();

        if (!year.equals("") && !month.equals("")) {
            if (Integer.parseInt(year) > LocalDate.now().getYear() || Integer.parseInt(year) < LocalDate.now().getYear() - 10) {
                Alerts.showWarning(yearWarningChart);
                return;
            }
            client.writeObject("showChart");
            client.writeObject(year + " " + month);
            ArrayList<Integer> data = client.inputList();

            initChart(data);

        } else {
            Alerts.showWarning(fieldWarningChart);
        }
    }

    @FXML
    void calProfit(ActionEvent event) {
        String year = yearField.getText();
        String month = monthBox.getValue();

        if (!year.equals("") && !month.equals("")) {
            if (Integer.parseInt(year) > LocalDate.now().getYear() || Integer.parseInt(year) < LocalDate.now().getYear() - 10) {
                Alerts.showWarning(yearWarningChart);
                return;
            }
            client.writeObject("showChart");
            client.writeObject(year + " " + month);
            ArrayList<Integer> data = client.inputList();

            int profit = data.get(0) - data.get(1);
            if (profit > 0) {
                resultProfit.setStyle("-fx-text-fill: green");
            } else {
                resultProfit.setStyle("-fx-text-fill: red");
            }
            resultProfit.setText(String.valueOf(profit));
        } else {
            Alerts.showWarning(fieldWarningChart);
        }
    }

    @FXML
    void createFile(ActionEvent event) {
        String year = yearField.getText();
        String month = monthBox.getValue();

        if (!year.equals("") && !month.equals("")) {
            if (Integer.parseInt(year) > LocalDate.now().getYear() || Integer.parseInt(year) < LocalDate.now().getYear() - 10) {
                Alerts.showWarning(yearWarningChart);
                return;
            }

            client.writeObject("showMonthStats");
            client.writeObject(year + " " + month);
            ArrayList<Selling> listSelling = client.inputList();
            ArrayList<Supplies> listSupplies = client.inputList();

            File.writeFile(listSelling, listSupplies, "Отчет за " + month + "." + year + ".txt");
            Alerts.showWarning(successCreateFile);
        } else {
            Alerts.showWarning(fieldWarningChart);
        }
    }

    @FXML
    void showTopProvider(ActionEvent event) {
        client.writeObject("showTopProvider");
        Providers provider = client.inputProvider();
        ObservableList<String> obs = FXCollections.observableArrayList();
        obs.add("Наименование: " + provider.getName());
        obs.add("Общая сумма сделок: " + provider.getSumSupply());
        resultList.setItems(obs);
    }

    @FXML
    void showSupplyTech(ActionEvent event) {
        client.writeObject("showSupplyTech");
        ArrayList<Technique> arrayList = client.inputList();
        ObservableList<String> obs = FXCollections.observableArrayList();
        for (Technique tech : arrayList) {
            obs.add(tech.getTypeName() + " " + tech.getBrand() + " " + tech.getModel() + ", " + tech.getQuantityInStock()+"шт.");
        }
        resultList.setItems(obs);
    }

    @FXML
    void showSaleTech(ActionEvent event) {
        client.writeObject("showSaleTech");
        ArrayList<Selling> arrayList = client.inputList();
        ObservableList<String> obs = FXCollections.observableArrayList();
        for (Selling sl : arrayList) {
            obs.add(sl.getTechnique().getTypeName() + " " + sl.getTechnique().getBrand() + " " + sl.getTechnique().getModel() + ", " +
                    sl.getQuantitySold() + "шт.");
        }
        resultList.setItems(obs);
    }

    @FXML
    private void initialize() {
        client = Client.getInstance();
        ObservableList<String> month = FXCollections.observableArrayList();
        month.addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        monthBox.setItems(month);
        monthBox.setValue(month.get(0));
    }

    private void initChart(ArrayList<Integer> data) {
        CategoryAxis xAxis = new CategoryAxis();
        XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();

        barChart.getData().clear();
        String m1 = "Продажи";
        String m2 = "Поставки";

        xAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList(m1, m2)));

        series1.getData().add(new XYChart.Data<>(m1, data.get(0)));
        series1.getData().add(new XYChart.Data<>(m2, data.get(1)));

        barChart.getData().add(series1);
    }
}
