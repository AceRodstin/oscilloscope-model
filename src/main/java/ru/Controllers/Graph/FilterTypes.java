package ru.Controllers.Graph;

public enum FilterTypes {
    NONE("Нет"), IIR("БИХ");

    private String typeName;

    FilterTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
