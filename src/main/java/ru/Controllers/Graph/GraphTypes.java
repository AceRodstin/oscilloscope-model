package ru.Controllers.Graph;

public enum GraphTypes {
    SIGNAL("Сигнал"), SPECTRUM("Спектр"), REGULATOR("Регулятор");

    private String typeName;

    GraphTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
