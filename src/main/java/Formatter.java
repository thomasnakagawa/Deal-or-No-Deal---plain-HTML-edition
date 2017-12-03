import org.apache.commons.lang.StringUtils;

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
        if (moneyAmount < 1.0f) {
            return "$" + String.format(java.util.Locale.US,"%.2f", moneyAmount);
        }else {
            return "$" + new DecimalFormat("#,###").format(moneyAmount);
        }
    }

    public static String addSpacePadding(String str) {
        String centeredString = str;
        if (str.length() >= 18) {
            return str;
        }
        int front = Math.floorDiv(18 - str.length(), 2);
        int end = 18 - str.length() - front;
        return StringUtils.repeat("&nbsp", front) + str + StringUtils.repeat("&nbsp", end);
    }
}
