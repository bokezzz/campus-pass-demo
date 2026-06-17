package org.example.demo.model;

/**
 * 预约审核状态枚举。
 *
 * <p>PENDING 表示待审核，APPROVED 表示已通过，REJECTED 表示未通过。
 * JSP 页面通过 label 显示中文状态。</p>
 */
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
