package org.example.demo.model;

public enum ReservationType {
    PUBLIC("社会公众预约"),
    OFFICIAL("公务预约");

    private final String label;

    ReservationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
