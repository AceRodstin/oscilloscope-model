package ru.Controllers.Regulator;

public enum NeededParameters {
    AMPLITUDE("Амплитуде"), DC("Статике"), FREQUENCY("Частоте"), RMS("RMS");

    private String parameterName;

    NeededParameters(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
}
