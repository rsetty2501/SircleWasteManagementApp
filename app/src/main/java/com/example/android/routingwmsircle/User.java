package com.example.android.routingwmsircle;

/**
 * Created by Rahul Setty on 3/8/2018.
 */

public class User {
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getGarbageCapacity() {
        return garbageCapacity;
    }

    public void setGarbageCapacity(int garbageCapacity) {
        this.garbageCapacity = garbageCapacity;
    }

    private String name;
    private String address;
    private String reply;
    private int garbageCapacity; // in liters

    public User(){

    }

    public User(String name, String address, String reply, int garbageCapacity){
        this.name = name;
        this.address = address;
        this.reply = reply;
        this.garbageCapacity = garbageCapacity;
    }
}
