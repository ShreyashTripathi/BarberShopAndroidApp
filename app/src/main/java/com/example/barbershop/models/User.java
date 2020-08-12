package com.example.barbershop.models;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String password;
    private String phone;
    private ArrayList<String> fav_shops;
    private String loginType;
    private String user_profile_pic;

    public User() {
    }

    public User(String name, String email, String password, String phone, ArrayList<String> fav_shops,String loginType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.fav_shops = fav_shops;
        this.loginType = loginType;
    }

    public User(String name, String email, String password, String phone, ArrayList<String> fav_shops, String loginType, String user_profile_pic) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.fav_shops = fav_shops;
        this.loginType = loginType;
        this.user_profile_pic = user_profile_pic;
    }

    public User(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getUser_profile_pic() {
        return user_profile_pic;
    }

    public void setUser_profile_pic(String user_profile_pic) {
        this.user_profile_pic = user_profile_pic;
    }

    public ArrayList<String> getFav_shops() {
        return fav_shops;
    }

    public void setFav_shops(ArrayList<String> fav_shops) {
        this.fav_shops = fav_shops;
    }

}
