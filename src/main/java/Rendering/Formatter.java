package Rendering;

import java.text.DecimalFormat;

public class Formatter {
    public static String formatMoney(float moneyAmount) {
        if (moneyAmount < 1.0f) {
            return "$" + String.format(java.util.Locale.US,"%.2f", moneyAmount);
        }else {
           return "$" + new DecimalFormat("#,###").format(moneyAmount);
        }
    }

    public static String formatMoney(double moneyAmount) {
        if (moneyAmount < 1.0) {
            return "$" + String.format(java.util.Locale.US,"%.2f", moneyAmount);
        }else {
            return "$" + new DecimalFormat("#,###").format(moneyAmount);
        }
    }
}
