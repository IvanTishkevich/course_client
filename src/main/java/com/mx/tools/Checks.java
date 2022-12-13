package com.mx.tools;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

public class Checks {
    public static Change Formatter(TextFormatter.Change change, int maxLen, String regex){
        int oldLength = change.getControlText().length();
        int newLength = change.getControlNewText().length();

        if (newLength < oldLength) return change;
        if (newLength == maxLen) return null;
        String str = change.getText();
        if (!str.matches(regex)) return null;

        boolean flag = str.equals(" ") || str.equals("-") || str.equals(".") || str.equals(",");
        boolean flagPointer = str.equals("-") || str.equals(".") || str.equals(",");

        try {
            if ((flag && change.getControlText().charAt(oldLength - 1) == '-')
                    || (flag && change.getControlText().charAt(oldLength - 1) == ' ')
                    || (flagPointer && change.getControlText().charAt(oldLength - 1) == '.')
                    || (flagPointer && change.getControlText().charAt(oldLength - 1) == ',')) {
                return null;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }

        return change;
    }
}
