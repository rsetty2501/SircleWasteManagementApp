package com.example.android.routingwmsircle;

import java.io.Serializable;
import java.util.ArrayList;

public class ClusterUserList implements Serializable{

    private String cluster;
    private ArrayList<UserInfoList> userInfoList;

    ClusterUserList(String cluster, ArrayList<UserInfoList> userInfoList){
        this.cluster = cluster;
        this.userInfoList = userInfoList;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public ArrayList<UserInfoList> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(ArrayList<UserInfoList> userInfoList) {
        this.userInfoList = userInfoList;
    }

}
