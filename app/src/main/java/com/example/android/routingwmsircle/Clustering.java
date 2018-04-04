package com.example.android.routingwmsircle;

import android.location.Address;
import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class Clustering {

    private static Map<String,Collection<Integer>> mapClusterNodes = new HashMap<>();
    private static List<Address> addressListNew;

    private Clustering(){

    }

    public  static Map<String,Collection<Integer>> sweepAlgoclustering(List<Address> address){

        addressListNew = address;
        double angle;
        // Map demand to polar angle
        Map<Double,Integer> mapDP = new HashMap<>();

        // First get all the polar angles for the points from the depot which is location 1
        for(int i = 0; i < address.size(); i++){

            if((i + 1) == address.size()){
                break;
            }
            else{
                angle = Math.atan2(address.get(i + 1).getLongitude() - address.get(0).getLongitude(),
                        address.get(i + 1).getLatitude() - address.get(0).getLatitude()) * 180 / Math.PI;

                Log.e(GeocodeConstants.TAG_ROUTE,"angle :" + angle);
                mapDP.put(angle,i);
            }
        }

        TreeMap<Double,Integer> treeMap = new TreeMap<>(mapDP);

        // Create a Map to map cluster and the list
        int c = 0, sum = 0;
        String cluster = "cluster" + c;
        Multimap<String,Integer> multimap = ArrayListMultimap.create();

        for(Map.Entry<Double,Integer> entry : treeMap.entrySet()){

            sum = sum + MainActivity.demandList.get(entry.getValue());
            Log.e(GeocodeConstants.TAG_ROUTE,"demand " + MainActivity.demandList.get(entry.getValue())
                    + " ,sum " + sum);

            if( sum <= MainActivity.vehicleCapacity){
                multimap.put(cluster,entry.getValue());
            }
            else{
                c++;
                cluster = "cluster" + c;
                sum = MainActivity.demandList.get(entry.getValue());
                multimap.put(cluster,entry.getValue());
            }
        }


        for(int i = 0; i < c + 1; i++){
            String clust = "cluster" + i;
            mapClusterNodes.put(clust,multimap.get(clust));
            Log.e(GeocodeConstants.TAG_ROUTE," cluster : " + multimap.get(clust));
        }

        Map<String,List<Address>> mapClusterAddress = new HashMap<>();


        for(Map.Entry<String,Collection<Integer>> entry: mapClusterNodes.entrySet()){
//            Log.e(GeocodeConstants.TAG_ROUTE, entry.getKey() + " : " + entry.getValue());
            List<Integer> list = new ArrayList<>(entry.getValue());
            List<Address> addressList = new ArrayList<>();
            for(int i = 0; i < list.size(); i++){
                int x = list.get(i) + 1;
                Log.e(GeocodeConstants.TAG_ROUTE, "New list : " + x);
                addressList.add(address.get(list.get(i) + 1));
                mapClusterAddress.put(entry.getKey(),addressList);
            }
        }

        for(Map.Entry<String,List<Address>> entry: mapClusterAddress.entrySet()){
            Log.e(GeocodeConstants.TAG_ROUTE, "Final address : " + entry.getKey() + " : " + entry.getValue().size());
        }

        return mapClusterNodes;
    }

    public  static Map<String,List<Address>> sweepAlgosegmentedAddress(){
        Map<String,List<Address>> mapClusterAddress = new HashMap<>();


        for(Map.Entry<String,Collection<Integer>> entry: mapClusterNodes.entrySet()){
//            Log.e(GeocodeConstants.TAG_ROUTE, entry.getKey() + " : " + entry.getValue());
            List<Integer> list = new ArrayList<>(entry.getValue());
            List<Address> addressList = new ArrayList<>();
            for(int i = 0; i < list.size(); i++){
                int x = list.get(i) + 1;
                Log.e(GeocodeConstants.TAG_ROUTE, "New list : " + x);
                addressList.add(addressListNew.get(list.get(i) + 1));
                mapClusterAddress.put(entry.getKey(),addressList);
            }
        }
        return mapClusterAddress;
    }
}
