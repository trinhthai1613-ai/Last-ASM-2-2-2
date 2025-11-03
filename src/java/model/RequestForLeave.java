/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author sonnt
 */
public class RequestForLeave extends BaseModel {

    public static final int STATUS_IN_PROGRESS = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    private Employee created_by;
    private Date created_time;
    private java.sql.Date from;
    private java.sql.Date to;
    private String reason;
    private int status;
    private Employee processed_by;
    private int reviewerLevel = -1;

    public Employee getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Employee created_by) {
        this.created_by = created_by;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public java.sql.Date getFrom() {
        return from;
    }

    public void setFrom(java.sql.Date from) {
        this.from = from;
    }

    public java.sql.Date getTo() {
        return to;
    }

    public void setTo(java.sql.Date to) {
        this.to = to;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Employee getProcessed_by() {
        return processed_by;
    }

    public void setProcessed_by(Employee processed_by) {
        this.processed_by = processed_by;
    }

    public int getReviewerLevel() {
        return reviewerLevel;
    }

    public void setReviewerLevel(int reviewerLevel) {
        this.reviewerLevel = reviewerLevel;
    }

    public boolean getCanProcess() {
        return reviewerLevel > 0 && status == STATUS_IN_PROGRESS;
    }

    public boolean isCreatedByViewer() {
        return reviewerLevel == 0;
    }

    public String getStatusLabel() {
        switch (status) {
            case STATUS_APPROVED:
                return "Approved";
            case STATUS_REJECTED:
                return "Rejected";
            default:
                return "In progress";
        }
    }
}
