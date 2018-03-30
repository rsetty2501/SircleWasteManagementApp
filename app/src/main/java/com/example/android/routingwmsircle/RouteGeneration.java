package com.example.android.routingwmsircle;

import android.location.Address;
import android.location.Location;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RouteGeneration {

    // cost matrix
    private static int cost[][];

    // Create a Map to map the costValue and the pathList
    private static Map<Integer,Integer> map= new HashMap<>();
    private static Map<Integer,Integer> map1 = new HashMap<>();

    private RouteGeneration(){

    }

    public static List<Address> OptimalRoute(List<Address> address){

        // Create a cost matrix which specifies the distance between the locations
        cost = new int[address.size()][address.size()];

        // Matrix to store path of optimal route
//        path = new int[address.size()][address.size()];
        List<Integer> path_inter = new ArrayList<>();
        List<Integer> path = new ArrayList<>();

        GeoPoint startPoint;
        GeoPoint endPoint;

        // Initialize the cost matrix to -1
        for(int i = 0; i < address.size(); i++){
            for(int j = 0; j < address.size(); j++){
                cost[i][j] = -1;
            }
        }

        // Assign the distance between the Geo points to the cost matrix
        for(int i = 0; i < address.size(); i++){
            for(int j = 0; j < address.size(); j++){

                if(i == j){
                    cost[i][j] = 0;
                }
                else{
                    startPoint = new GeoPoint(address.get(i).getLatitude(),address.get(i).getLongitude());
                    endPoint = new GeoPoint(address.get(j).getLatitude(),address.get(j).getLongitude());

                    Location location1 = new Location("");
                    location1.setLatitude(startPoint.getLatitude());
                    location1.setLongitude(startPoint.getLongitude());

                    Location location2 = new Location("");
                    location2.setLatitude(endPoint.getLatitude());
                    location2.setLongitude(endPoint.getLongitude());
                    double distance = location1.distanceTo(location2);

                    Log.e(GeocodeConstants.TAG_ROUTE,"time location 1 : " );
                    Log.e(GeocodeConstants.TAG_ROUTE,"time location 2 : " + location2.getTime());

                    cost[i][j] = (int)distance;
                    Log.e(GeocodeConstants.TAG_ROUTE,"cost from " + i + " to " + j + " : " + cost[i][j]);
                }
            }
        }



        // Create a node array
        List<Integer> nodearr = new ArrayList<>();
        for(int i = 0; i < address.size() - 1; i++){
            nodearr.add(i + 1);
//            Log.e(GeocodeConstants.TAG_ROUTE,"Node arr : " + nodearr.get(i));
        }

        // Compute the Travelling salesman problem
        int result = TSP(0, nodearr);
        Log.e(GeocodeConstants.TAG_ROUTE,"Minimum route : " + result);

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Log.e(GeocodeConstants.TAG_ROUTE, entry.getKey() + " : " + entry.getValue());
        }

        for (Map.Entry<Integer, Integer> entry : map1.entrySet()) {
            Log.e(GeocodeConstants.TAG_ROUTE, "Path : "+entry.getKey() + " : " + entry.getValue());
        }

        int val = 0;
        // First, get the mapping elements and store in a list
        for(int i = 0; i < map.size(); i++){
            val = searchMap(result);
            Log.e(GeocodeConstants.TAG_ROUTE, "Value intermediate :" + val);

            if(val == 0){
                break;
            }
            else{
                path_inter.add(val);
                result = val;
            }
        }

        // display the contents of the path
//        for(int i = 0; i < path_inter.size(); i++){
//            Log.e(GeocodeConstants.TAG_ROUTE, "Path inter value : " + path_inter.get(i));
//        }

        // Now, get the path sequence
        for(int i = 0; i < path_inter.size(); i++)
        {
            Log.e(GeocodeConstants.TAG_ROUTE, "Path inter value : " + path_inter.get(i));
            for (Map.Entry<Integer, Integer> entry : map1.entrySet()) {

                Log.e(GeocodeConstants.TAG_ROUTE, "Enter map1 : " + entry.getKey());
                if(path_inter.get(i).equals(entry.getKey())){
                    Log.e(GeocodeConstants.TAG_ROUTE, "Path value: "+entry.getValue());
                    path.add(entry.getValue());
                }
            }
        }

        for(int i = 0; i < path.size(); i++){
            Log.e(GeocodeConstants.TAG_ROUTE, "Path inter value : " + path.get(i));
        }


//        for(int i =0; i < pathList.size();i++)
//        {
//            Log.e(GeocodeConstants.TAG_ROUTE,"path : "+pathList.get(i));
//        }

//        for(int i = 0; i < pathList.size(); i++){
//            Log.e(GeocodeConstants.TAG_ROUTE,"Path : " + i + " " + pathList.get(i));
//        }
//
//        // Need to find the sequence of path based on the minimum route
//        // Size of path list
//        int size = address.size() + nodearr.size();
//        Log.e(GeocodeConstants.TAG_ROUTE,"Size : " + size);
//        int pathListsize = nodearr.size() * (size);
//        Log.e(GeocodeConstants.TAG_ROUTE,"Path size : " + pathListsize);
//        int k;
//        List<Integer> minPath = new ArrayList<>();
//
//        // Search for the final cost for each path
//        for(int i = size - 1; i < pathListsize - 1; i = i + size){
//            if(result == pathList.get(i)){
//                Log.e(GeocodeConstants.TAG_ROUTE,"Location of min cost in array : " + i);
//                k = i - (size - 1);
//                Log.e(GeocodeConstants.TAG_ROUTE,"k value : " + k);
//                for(int j = k; j < address.size(); j++){
//                    Log.e(GeocodeConstants.TAG_ROUTE,"Final min path : " + pathList.get(j));
//                    minPath.add(pathList.get(j));
//                }
//
//            }
//        }
//
        // Return the new sequence of address
        List<Address> newAdd = new ArrayList<>();
        int k = 0;
        newAdd.add(address.get(0));
        for(int i = 0; i < path.size(); i++){
            k = path.get(i);
            newAdd.add(address.get(k));
        }
        newAdd.add(address.get(0));
        Log.e(GeocodeConstants.TAG_ROUTE,"Optimal address size : " + newAdd.size());

        return newAdd;
    }

    private static int searchMap(int key){

        int value = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Log.e(GeocodeConstants.TAG_ROUTE, entry.getKey() + " : " + entry.getValue());

            if(key == entry.getKey()){
                value =  entry.getValue();
                Log.e(GeocodeConstants.TAG_ROUTE, " step 1 : " + value);
            }
        }
        return value;
    }



    private static int TSP(int start, List<Integer> nodearr){


        int costValue = 0;
        List<Integer> tempCost = new ArrayList<>();

        // Create an array for !intersection
        List<Integer> set1 = new ArrayList<>();
        List<Integer> set2;

        // Initail condition for tsp f(i,phi) = Cij
        if(nodearr.get(0) == -1){
            Log.e(GeocodeConstants.TAG_ROUTE,"Start : " + start);

//            Log.e(GeocodeConstants.TAG_ROUTE,"Cost value : " + cost[start][0]);
            return cost[start][0];
        }
        else{

            for(int i = 0; i < nodearr.size(); i++){

                set1.add(start);
                set1.add(nodearr.get(i));

                Log.e(GeocodeConstants.TAG_ROUTE,"Start : " + start);

                set2 = intersection(set1,nodearr);
                set1.clear();

                int x = TSP(nodearr.get(i),set2);
                costValue = cost[start][nodearr.get(i)] + x;

                map.put(costValue,x);
                map1.put(x,nodearr.get(i));

                tempCost.add(costValue);

            }
            Collections.sort(tempCost);
            Log.e(GeocodeConstants.TAG_ROUTE,"Cost value : " + tempCost.get(0));
            // return the minimum value
            return tempCost.get(0);
        }
    }

    private static List<Integer> intersection(List<Integer> arr1, List<Integer> arr2){

        List<Integer> arrIntersect = new ArrayList<>();
        for(int i : arr2){
            if(!arr1.contains(i)){
                arrIntersect.add(i);
            }
        }
        if(arrIntersect.isEmpty()){
            arrIntersect.add(-1);
        }
        return arrIntersect;

    }
}
