package com.example.barbershop.models;

public class BarberService {
    private String service_name;
    private String service_price;
    private String service_img;

    public BarberService() {
    }

    public BarberService(String service_name, String service_price,String service_img) {
        this.service_name = service_name;
        this.service_price = service_price;
        this.service_img = service_img;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getService_img() {
        return service_img;
    }

    public void setService_img(String service_img) {
        this.service_img = service_img;
    }
}
