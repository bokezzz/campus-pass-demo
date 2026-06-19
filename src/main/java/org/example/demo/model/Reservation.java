package org.example.demo.model;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private ReservationType type;
    private ReservationStatus status;
    private LocalDateTime applyTime;
    private String campus;
    private LocalDateTime visitTime;
    private String organization;
    private String visitorName;
    private String identityNo;
    private String phone;
    private String trafficType;
    private String carNo;
    private String companions;
    private Long visitDepartmentId;
    private String visitDepartmentName;
    private String hostName;
    private String reason;
    private String reviewComment;
    private LocalDateTime reviewTime;
    private String passCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReservationType getType() {
        return type;
    }

    public void setType(ReservationType type) {
        this.type = type;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalDateTime visitTime) {
        this.visitTime = visitTime;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(String trafficType) {
        this.trafficType = trafficType;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getCompanions() {
        return companions;
    }

    public void setCompanions(String companions) {
        this.companions = companions;
    }

    public Long getVisitDepartmentId() {
        return visitDepartmentId;
    }

    public void setVisitDepartmentId(Long visitDepartmentId) {
        this.visitDepartmentId = visitDepartmentId;
    }

    public String getVisitDepartmentName() {
        return visitDepartmentName;
    }

    public void setVisitDepartmentName(String visitDepartmentName) {
        this.visitDepartmentName = visitDepartmentName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }
}
