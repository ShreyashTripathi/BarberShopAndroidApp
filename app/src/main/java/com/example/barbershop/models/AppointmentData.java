package com.example.barbershop.models;

public class AppointmentData {
    private String shopId;
    private String userId;
    private String service_opted;
    private String workerId;
    private String date;
    private String timeSlot;
    private boolean active_status = true;

    public AppointmentData() {
    }

    public AppointmentData(String shopId, String userId, String service_opted, String workerId, String date, String timeSlot, boolean active_status) {
        this.shopId = shopId;
        this.userId = userId;
        this.service_opted = service_opted;
        this.workerId = workerId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.active_status = active_status;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getService_opted() {
        return service_opted;
    }

    public void setService_opted(String service_opted) {
        this.service_opted = service_opted;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isActive_status() {
        return active_status;
    }

    public void setActive_status(boolean active_status) {
        this.active_status = active_status;
    }

    @Override
    public String toString() {
        return "AppointmentData{" +
                "shopId='" + shopId + '\'' +
                ", userId='" + userId + '\'' +
                ", service_opted='" + service_opted + '\'' +
                ", workerId='" + workerId + '\'' +
                ", date='" + date + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", active_status=" + active_status +
                '}';
    }
}
