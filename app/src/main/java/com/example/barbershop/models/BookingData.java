package com.example.barbershop.models;

public class BookingData {
    private String user_name;
    private String user_phone;
    private String user_img;
    private String date;
    private String timeSlot;
    private String hairstylist_id;

    public BookingData() {
    }

    public BookingData(String user_name, String user_phone, String user_img, String date, String timeSlot, String hairstylist_id) {
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_img = user_img;
        this.date = date;
        this.timeSlot = timeSlot;
        this.hairstylist_id = hairstylist_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
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

    public String getHairstylist_id() {
        return hairstylist_id;
    }

    public void setHairstylist_id(String hairstylist_id) {
        this.hairstylist_id = hairstylist_id;
    }
}
