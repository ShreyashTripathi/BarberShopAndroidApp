package com.example.barbershop.models;

public class HairStylist {
    private int profile_pic;
    private float rating;
    private String phone_no;
    private String name;

    public HairStylist() {
    }

    public HairStylist(int profile_pic, float rating, String phone_no, String name) {
        this.profile_pic = profile_pic;
        this.rating = rating;
        this.phone_no = phone_no;
        this.name = name;
    }

    public int getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(int profile_pic) {
        this.profile_pic = profile_pic;
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
}
