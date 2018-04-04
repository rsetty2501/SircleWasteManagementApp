package com.example.android.routingwmsircle;

import java.io.Serializable;

/**
 * Created by Rahul Setty on 2/22/2018.
 */

public class UserInfoList implements Serializable{

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

    double getAddress_latitude() {
        return address_latitude;
    }

    double getAddress_longitude() {
        return address_longitude;
    }

}
