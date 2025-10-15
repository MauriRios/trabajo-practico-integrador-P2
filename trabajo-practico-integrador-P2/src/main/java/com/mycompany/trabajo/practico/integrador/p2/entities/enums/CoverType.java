package com.mycompany.trabajo.practico.integrador.p2.entities.enums;

public enum CoverType {
    RC("RC"),
    TERCEROS("Contra terceros"),
    TODO_RIESGO("Todo_riesgo");

    private final String description;

    CoverType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
