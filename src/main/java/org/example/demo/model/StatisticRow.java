package org.example.demo.model;

public class StatisticRow {
    private String label;
    private int reservations;
    private int people;

    public StatisticRow(String label, int reservations, int people) {
        this.label = label;
        this.reservations = reservations;
        this.people = people;
    }

    public String getLabel() {
        return label;
    }

    public int getReservations() {
        return reservations;
    }

    public int getPeople() {
        return people;
    }
}
