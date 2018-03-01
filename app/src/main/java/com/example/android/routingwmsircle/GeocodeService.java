package com.example.android.routingwmsircle;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rahul Setty on 2/21/2018.
 */

public class GeocodeService extends IntentService{
    // Create an object for the result receiver
    protected ResultReceiver resultReceiver;

    public GeocodeService() {
        super("GeocodeService");
    }

    // The intent is coming from the Main activity
    // With that intent, we need to perform the Geocode operation
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v(GeocodeConstants.TAG_GEOCODE,"onHandle");

        // Create an object for Geocoder
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        // Object to store list of addresses returned by geocoder
        ArrayList<List<Address>> addresses = new ArrayList<>();

        // String for error message
        String errorMessage = "";

        // Retrieve the address name entered by user and pass it to geocoder
        ArrayList<String> addressName = intent.getStringArrayListExtra(GeocodeConstants.LOCATION_NAME);

        Log.v(GeocodeConstants.TAG_GEOCODE,"Address test : " + addressName.get(0));

        for(int i = 0; i < addressName.size(); i++)
        {
            try{
                List<Address> test = geocoder.getFromLocationName(addressName.get(i),1);
                Log.v(GeocodeConstants.TAG_GEOCODE,"test 2 : " + test.get(0));
                addresses.add(test);
            }catch (Exception e){
                errorMessage = "Service not available";
                Log.e(GeocodeConstants.TAG_GEOCODE,errorMessage,e);
            }
        }


        // Extract the resultreceiver object from Mainactivity
        // intent.getParcelableExtra() is the method to get the object
        resultReceiver = intent.getParcelableExtra(GeocodeConstants.ADDRESS_RECEIVER);

        // Scenario when the addresses is null or empty
        if(addresses == null || addresses.size() == 0){
            errorMessage = "Latitude/Longitude not found";
            Log.e(GeocodeConstants.TAG_GEOCODE,errorMessage);
            deliverResultToMainActivity(GeocodeConstants.FAILURE,errorMessage,null);
        }
        else{

            List<Address> address = new ArrayList<Address>();
            // Get the first address
            for (int i = 0; i < addresses.size(); i++){
                address.add(addresses.get(i).get(0));
            }

            // To check what the above instruction returns
            Log.v(GeocodeConstants.TAG_GEOCODE,"Address : " + String.valueOf(addresses.get(0)));

            deliverResultToMainActivity(GeocodeConstants.SUCCESS,
                    "Lat/Log found",address);
        }
    }

    private void deliverResultToMainActivity(int resultCode, String message, List<Address> address){

        // Create a bundle
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(GeocodeConstants.RES_ADDRESS, (ArrayList<? extends Parcelable>) address);
        bundle.putString(GeocodeConstants.MESSAGE,message);
        resultReceiver.send(resultCode,bundle);

    }
}
