package com.example.android.routingwmsircle;

import android.content.Intent;
import android.location.Address;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

     // Declare a variiable for address receiver
    protected AddressReceiver addressReceiver;

    // Text view
    TextView displayView;

    // List view
    ListView listView;

    // array list for user information
    ArrayList<UserInfoList> userInfoLists = new ArrayList<>();

    // Create an user info adapter variable
    UserInfoListAdapter userInfoListAdapter;

    // Need to make the address list with Latitude and Longitude for route
    public static List<Address> addressLatLong = new ArrayList<>();

    // List of strings to store the addresses
    static final ArrayList<String> addressList = new ArrayList<>();

    // List of string to store name of user
    static final List<String> nameList = new ArrayList<>();

    // List to store the demands of the customer
    static final List<Integer> demandList = new ArrayList<>();

    // Vehicle capacity
    static final int vehicleCapacity = 40;

    // store count
    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view_user);
        displayView = (TextView) findViewById(R.id.text_view_main);

        Log.e(GeocodeConstants.TAG_MAIN, "Entered onCreate method!! ");


        // Setup FAB to open Route map
        FloatingActionButton mapFab = (FloatingActionButton) findViewById(R.id.map);
        mapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RouteMap.class);
                startActivity(intent);
            }
        });

        // Instantiate the address receiver
        addressReceiver = new AddressReceiver(null);

        // Add dummy data
        queryDatabase();

        Log.e(GeocodeConstants.TAG_MAIN, "Point after query database!! ");
        Log.e(GeocodeConstants.TAG_MAIN, "Inside count!! " + count);
        if(count > 0){
            Log.e(GeocodeConstants.TAG_MAIN, "Inside count!! " + count);
            sendAddressForGeocode();
        }

    }

    protected void queryDatabase(){

//        FirebaseApp.initializeApp(this);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference =
//                firebaseDatabase.getReference("08-Mar-2018");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User userValue = null;
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    userValue = userSnapshot.getValue(User.class);
//                    if (userValue.getReply().equals("Yes")) {
//
//                        addressList.add(userValue.getAddress());
//                        nameList.add(userValue.getName());
//                        count = count + 1;
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                    Log.w(GeocodeConstants.TAG_MAIN, "Failed to read value.", error.toException());
//            }
//        });

        // Adding dummy data manually to check if the app works properly
        addressList.add("Sernanders väg 4, 752 61 Uppsala");
        addressList.add("Uppsala");
        addressList.add("Lägerhyddsvägen 2, 752 37 Uppsala");
        addressList.add("Flogstavägen 5, 752 73 Uppsala");
        addressList.add("Kantorsgatan 44, 754 24 Uppsala");

        nameList.add("Micheal");
        nameList.add("Tom");
        nameList.add("Dick");
        nameList.add("Harry");
        nameList.add("Patrick");

        // Assuming 1st address is the depot of the trucks
        demandList.add(20);
        demandList.add(30);
        demandList.add(20);
        demandList.add(10);

        count = 5;

        Log.e(GeocodeConstants.TAG_MAIN, "Count is: " + count);

    }


    private void sendAddressForGeocode(){

        try{

            // Now create an intent for the Geocode
            Intent intent = new Intent(getApplicationContext(), GeocodeService.class);
            intent.putExtra(GeocodeConstants.ADDRESS_RECEIVER,addressReceiver);
            intent.putStringArrayListExtra(GeocodeConstants.LOCATION_NAME, addressList);
            Log.v(GeocodeConstants.TAG_MAIN,"Starting the service");
            startService(intent);
        }
        finally {

        }

    }

    // This code is executed when the finish() method is called from Route Map
    @Override
    protected  void onStart(){
        super.onStart();
//        listView.setVisibility(View.INVISIBLE);
        queryDatabase();

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the users database.
     */
    private void displayDatabaseInfo() {

        displayView.setText("Add user information to produce optimized route to collect garbage");
    }

    private void deleteAllEntries(){

        listView.setVisibility(View.INVISIBLE);
        displayDatabaseInfo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // This is for the Geo coding part
    public class AddressReceiver extends ResultReceiver{

        public AddressReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult (final int resultCode, final Bundle resultData){

            // If it is a success
            if(resultCode == GeocodeConstants.SUCCESS){

                addressLatLong = resultData.getParcelableArrayList(GeocodeConstants.RES_ADDRESS);

                for (int i = 0; i < addressLatLong.size(); i++){

                    String name = nameList.get(i);
                    String addList = addressList.get(i);
                    double lat = addressLatLong.get(i).getLatitude();
                    double longitude = addressLatLong.get(i).getLongitude();

                    userInfoLists.add(new UserInfoList(name, addList, lat, longitude));

                }
                userInfoListAdapter = new UserInfoListAdapter(getApplicationContext(),userInfoLists);

                // Run the operation on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        displayDatabaseInfo();
                        displayView.setVisibility(View.INVISIBLE);
                        // Set the TextView with Latitude and Longitude
                        // This loop is to iterate through all the rows of the table

                        if(userInfoListAdapter != null){
                            Log.v(GeocodeConstants.TAG_MAIN,"Inside adapter!");
                            listView.setAdapter(userInfoListAdapter);

                            Log.v(GeocodeConstants.TAG_MAIN,"Inside adapter 2!");
                        }
                        else{
                            Log.v(GeocodeConstants.TAG_MAIN,"Outside adapter!");
                        }

                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        displayDatabaseInfo();
                        displayView.append("No Data!");
                    }
                });
            }
        }
    }
}
