package com.example.barbershop.models;

public class Worker {
    private String profile_pic_url;
    private float rating;
    private String phone_no;
    private String name;
    private String shop_name;
    private String gender;
    private String shop_type;

    public Worker() {
    }

    public Worker(String profile_pic_url, float rating, String phone_no, String name, String shop_name, String gender, String shop_type) {
        this.profile_pic_url = profile_pic_url;
        this.rating = rating;
        this.phone_no = phone_no;
        this.name = name;
        this.shop_name = shop_name;
        this.gender = gender;
        this.shop_type = shop_type;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getShop_type() {
        return shop_type;
    }

    public void setShop_type(String shop_type) {
        this.shop_type = shop_type;
    }
}
