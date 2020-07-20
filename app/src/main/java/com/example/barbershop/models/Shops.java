package com.example.barbershop.models;

import java.util.ArrayList;

public class Shops{

    private String shopName;
    private String location;
    private double latitude;
    private double longitude;
    private String about;
    private float ratings;
    private ArrayList<BarberService> services;
    private String phone;
    private int waiting_list;
    private String type;
    private String shop_img;

    public Shops(String shopName, String location, double latitude, double longitude, String about, float ratings, ArrayList<BarberService> services, String phone, int waiting_list, String type, String shop_img) {
        this.shopName = shopName;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.about = about;
        this.ratings = ratings;
        this.services = services;
        this.phone = phone;
        this.waiting_list = waiting_list;
        this.type = type;
        this.shop_img = shop_img;
    }

    public Shops() {
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public ArrayList<BarberService> getServices() {
        return services;
    }

    public void setServices(ArrayList<BarberService> services) {
        this.services = services;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getWaiting_list() {
        return waiting_list;
    }

    public void setWaiting_list(int waiting_list) {
        this.waiting_list = waiting_list;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShop_img() {
        return shop_img;
    }

    public void setShop_img(String shop_img) {
        this.shop_img = shop_img;
    }
}
