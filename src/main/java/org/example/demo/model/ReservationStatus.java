package org.example.demo.model;

public enum ReservationStatus {
    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("未通过");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
