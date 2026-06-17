package org.example.demo.model;

/**
 * 预约类型枚举。
 *
 * <p>PUBLIC 表示社会公众预约，提交后自动通过；
 * OFFICIAL 表示公务预约，需要后台审核。</p>
 */
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
