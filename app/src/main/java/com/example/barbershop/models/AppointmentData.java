package com.example.barbershop.models;

public class AppointmentData {
    private String shopName;
    private String date;
    private String timeSlot;
    private String hairstylistName;
    private boolean active;

    public AppointmentData() {
    }

    public AppointmentData(String shopName, String date, String timeSlot, String hairstylistName, boolean active) {
        this.shopName = shopName;
        this.date = date;
        this.timeSlot = timeSlot;
        this.hairstylistName = hairstylistName;
        this.active = active;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
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

    public String getHairstylistName() {
        return hairstylistName;
    }

    public void setHairstylistName(String hairstylistName) {
        this.hairstylistName = hairstylistName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
