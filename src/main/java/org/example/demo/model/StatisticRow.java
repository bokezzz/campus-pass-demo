package org.example.demo.model;

/**
 * 后台统计结果行。
 *
 * <p>label 是统计维度名称，例如某个月、某个校区或某个部门；
 * reservations 是预约次数，people 是预约人次。</p>
 */
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
