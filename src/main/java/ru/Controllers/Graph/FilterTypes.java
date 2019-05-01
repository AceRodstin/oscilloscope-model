package ru.Controllers.Graph;

public enum FilterTypes {
    NONE("Нет");

    private String typeName;

    FilterTypes(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
