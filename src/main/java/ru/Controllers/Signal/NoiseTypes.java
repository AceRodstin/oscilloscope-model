package ru.Controllers.Signal;

public enum NoiseTypes {
    NONE("Нет"), LOW("Низкая"), MIDDLE("Средняя"), HIGH("Высокая");

    private String typeName;

    NoiseTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
