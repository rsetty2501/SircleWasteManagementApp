package com.example.android.routingwmsircle;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.routingwmsircle.data.UserDbHelper;
import com.example.android.routingwmsircle.data.UserInfo;

import org.osmdroid.util.constants.GeoConstants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    protected static UserDbHelper userDbHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view_user);
        displayView = (TextView) findViewById(R.id.text_view_main);

        // Instantiate the database
        userDbHelper = new UserDbHelper(this, UserDbHelper.DATABASE_NAME,null,UserDbHelper.DATABASE_VERSION);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

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

        // Create an intent to send the address text and the addressReceiver object
        Cursor cursor = queryDatabase();
        if(cursor.getCount() > 0){
            sendAddressForGeocode();
        }

    }

    static protected Cursor queryDatabase(){

        // Create and/or open a database to read from it
        // This is the object where we connect it to the from activity to the database
        SQLiteDatabase db = userDbHelper.getReadableDatabase();

        // Perform query method simiar to "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        String[] projection = {
                UserInfo.UserEntry._ID,
                UserInfo.UserEntry.COLUMN_USER_NAME,
                UserInfo.UserEntry.COLUMN_USER_ADDRESS,
                UserInfo.UserEntry.COLUMN_USER_REPLY
        };

        // Filter for reply with "Yes"
        String selection = UserInfo.UserEntry.COLUMN_USER_REPLY + "=?";
        String[] selectionArgs = new String[]{String.valueOf(UserInfo.UserEntry.YES)};

        Cursor cursor = db.query(UserInfo.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);
        return cursor;

    }

    private void sendAddressForGeocode(){

        // 1. Extract only those addresses with a reply "Yes"

        // List of strings to store the addresses
        ArrayList<String> addressList = new ArrayList<>();

        Cursor cursor = queryDatabase();

        try{
            int col_address = cursor.getColumnIndex(UserInfo.UserEntry.COLUMN_USER_ADDRESS);

            // This loop is to iterate through all the rows of the table
            while (cursor.moveToNext()){
                addressList.add(cursor.getString(col_address));
            }

            // Now create an intent for the Geocode
            Intent intent = new Intent(getApplicationContext(), GeocodeService.class);
            intent.putExtra(GeocodeConstants.ADDRESS_RECEIVER,addressReceiver);
            intent.putStringArrayListExtra(GeocodeConstants.LOCATION_NAME, addressList);
            Log.v(GeocodeConstants.TAG_MAIN,"Starting the service");
            startService(intent);
        }
        finally {
            cursor.close();
        }

    }

    // This code is executed when the finish() method is called from EditorActivity
    @Override
    protected  void onStart(){
        super.onStart();
        displayDatabaseInfo();

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the users database.
     */
    private void displayDatabaseInfo() {

//            displayView.setText("Display User info with reply Yes" + "\n");
//            displayView.append(UserInfo.UserEntry.COLUMN_USER_NAME + "\t \t"
//                    + UserInfo.UserEntry.COLUMN_USER_ADDRESS + "\t \t"
//                    + "Latitude" + "\t \t"
//                    + "Longitude" + "\n");

        displayView.setText("Add user information to produce optimized route to collect garbage");
    }

    private void deleteAllEntries(){

        SQLiteDatabase db = userDbHelper.getWritableDatabase();
        db.execSQL(UserDbHelper.SQL_DELETE_TABLE);
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

                Cursor cursor = queryDatabase();
                int col_name = cursor.getColumnIndex(UserInfo.UserEntry.COLUMN_USER_NAME);
                int col_address = cursor.getColumnIndex(UserInfo.UserEntry.COLUMN_USER_ADDRESS);

                final List<String> addressList = new ArrayList<>();
                final List<String> nameList = new ArrayList<>();

                // Store all the database data in the List
                while (cursor.moveToNext()){
                    nameList.add(cursor.getString(col_name));
                    addressList.add(cursor.getString(col_address));
                }

                for (int i = 0; i < addressLatLong.size(); i++){
//                            displayView.append(nameList.get(i) + "\t \t"
//                                + addressList.get(i) + "\t \t"
//                                + addressLatLong.get(i).getLongitude() + "\t \t"
//                                + addressLatLong.get(i).getLatitude() + "\n");

                    String n = nameList.get(i);
                    String addList = addressList.get(i);
                    double lat = addressLatLong.get(i).getLatitude();
                    double longitude = addressLatLong.get(i).getLongitude();

                    userInfoLists.add(new UserInfoList(n, addList, lat, longitude));

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
