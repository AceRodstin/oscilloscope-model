package ru.Models;

public class GraphModel {
    private double lowerBound;
    private double scale;
    private double tickUnit;
    private double upperBound;
    private String valueName;

    public void calculateBounds() {
        if (valueName.equals("В")) {
            lowerBound = -scale * 5;
            tickUnit = scale;
            upperBound = scale * 5;
        } else if (valueName.equals("с") || valueName.equals("Гц")) {
            lowerBound = 0;
            tickUnit = scale;
            upperBound = scale * 10;
        }

    }

    public void parseScale(String scale) {
        String[] separatedScale = scale.split(" ");
        double digits = Double.parseDouble(separatedScale[0]);
        String suffix = separatedScale[1].substring(0, 1);
        separatedScale = scale.split("/дел");

        if (suffix.equals("м")) {
            this.scale = digits * 0.001;
            valueName = separatedScale[0].substring(separatedScale[0].length() - 1);
        } else {
            this.scale = digits;
            valueName = separatedScale[0].split( " ")[1];
        }
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public double getTickUnit() {
        return tickUnit;
    }

    public double getUpperBound() {
        return upperBound;
    }
}