package ru.Utils;

public class Utils {
    private Utils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }
}
