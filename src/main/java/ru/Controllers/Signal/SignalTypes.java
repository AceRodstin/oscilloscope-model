package ru.Controllers.Signal;

public enum SignalTypes {
    SINE("Синусоида"), PULSE("Импульсный"), TRIANGLES("Треугольный"), SAW("Пила"), NOISE("Шум");

    private String typeName;

    SignalTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}