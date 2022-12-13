package com.mx.tools;

import com.mx.logic.client.Client;
import com.mx.data.Selling;
import com.mx.data.Supplies;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;

public class File {
    public static void writeFile(ArrayList<Selling> listSelling, ArrayList<Supplies> listSupplies, String fileName) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("Отчеты/"+fileName, false));
            bw.write("Дата создания отчета: " + LocalDate.now() + "\n");
            bw.write("Фамилия имя: " + Client.getInstance().getUser().getName() + " " +
                    Client.getInstance().getUser().getSurname() + "\n\n");

            if (!listSelling.isEmpty()) {
                writeSelling(bw, listSelling);
            } else {
                bw.write("\n\t\tНет данных о продажах!\n");
            }

            if (!listSupplies.isEmpty()) {
                writeSupplies(bw, listSupplies);
            }else{
                bw.write("\n\t\tНет данных о поставках!\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeSelling(BufferedWriter bw, ArrayList<Selling> listSelling) throws IOException {
        Formatter formatter = new Formatter();
        String format = "%20s %20s %15s %15s %15s";

        formatter.format("%45s", "Продажи");
        bw.write(formatter + "\n\n");

        formatter = new Formatter();
        formatter.format(format, "Бренд",
                "Модель", "Цена", "Количество", "Дата");
        bw.write(formatter + "\n");

        for (Selling sl : listSelling) {
            formatter = new Formatter();
            formatter.format(format, sl.getTechnique().getBrand(),
                    sl.getTechnique().getModel(), sl.getSalePrice(), sl.getQuantitySold(), sl.getSaleDate());
            bw.write(formatter + "\n");
        }
        formatter.close();
    }

    private static void writeSupplies(BufferedWriter bw, ArrayList<Supplies> listSupplies) throws IOException {
        Formatter formatter = new Formatter();

        formatter = new Formatter();
        formatter.format("%45s", "Поставки");
        bw.write("\n" + formatter + "\n\n");

        String format = "%20s %20s %15s %15s %15s %20s";
        formatter = new Formatter();
        formatter.format(format, "Бренд",
                "Модель", "Дата", "Цена", "Количество", "Поставщик");
        bw.write(formatter + "\n");

        for (Supplies sup : listSupplies) {
            formatter = new Formatter();
            formatter.format(format, sup.getTechnique().getBrand(),
                    sup.getTechnique().getModel(), sup.getSupplyDate(), sup.getPurchasePrice(), sup.getQuantitySupplied(),
                    sup.getProvider().getName());
            bw.write(formatter + "\n");
        }
        formatter.close();
    }
}
