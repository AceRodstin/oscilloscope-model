package ru.Utils;

public class Utils {
    private static final int DECIMAL_SCALE_LIMIT = 7; // максимальное количество знаков после запятой

    public static double roundValue(double value, int rounder) {
        return (double) Math.round(value * rounder) / rounder;
    }

    public static String convertFromExponentialFormat(double value, int decimalFormatScale) {
        int scale = (int) Math.log10(decimalFormatScale);
        String convertedValue = String.format("%.7f", value);
        return convertedValue.substring(0, convertedValue.length() - (DECIMAL_SCALE_LIMIT - scale));
    }

    private Utils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    public static int getDecimalScaleLimit() {
        return DECIMAL_SCALE_LIMIT;
    }
}
