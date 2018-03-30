package com.example.android.routingwmsircle;

/**
 * Created by Rahul Setty on 2/22/2018.
 */

public class UserInfoList {

    private String name;
    private String address;
    private double address_latitude;
    private double address_longitude;

    UserInfoList(String name, String address, double address_latitude, double address_longitude){
        this.name = name;
        this.address = address;
        this.address_latitude = address_latitude;
        this.address_longitude = address_longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    double getAddress_latitude() {
        return address_latitude;
    }

    public void setAddress_latitude(double address_latitude) {
        this.address_latitude = address_latitude;
    }

    double getAddress_longitude() {
        return address_longitude;
    }

    public void setAddress_longitude(double address_longitude) {
        this.address_longitude = address_longitude;
    }

}
