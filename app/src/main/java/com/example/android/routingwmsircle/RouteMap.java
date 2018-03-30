package com.example.android.routingwmsircle;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.mapsforge.map.datastore.MultiMapDataStore;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.QuadTreeTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * Created by Rahul Setty on 2/22/2018.
 */

public class RouteMap extends AppCompatActivity{

    // boolean flag to check if there is marker or not
    protected boolean flag;

    // text view
    TextView textView;

    // Address list with Latitude and Longitude
    List<Address> address = new ArrayList<>();

    // Optimal address
    List<Address> optimalAddress = new ArrayList<>();

    // Create a variable to map cluster and list of Nodes
    Map<String,List<Address>> mapClusterAddress = new HashMap<>();

    // test address
    List<Address> testAddress = new ArrayList<>();

    MapView mapView = null;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //inflate and create the map
        setContentView(R.layout.activity_mapview);
        textView = findViewById(R.id.text_view_map);


        // Initialize the address
        address = MainActivity.addressLatLong;

        // Create clusters for segmentation
        mapClusterAddress = Clustering.sweepAlgoclustering(address);

        // View of uppsala in map
        uppsalaMapView();

        // Setting markers for garbage collection
        flag = settingMarkerinMap();

        // Add scale bar
        scaleBar();

        for(Map.Entry<String,List<Address>> entry: mapClusterAddress.entrySet()){
            if(entry.getKey().equals("cluster2")){
                testAddress.add(address.get(0));
                for(int i = 0; i < entry.getValue().size(); i++){
                    testAddress.add(entry.getValue().get(i));
                }
            }
        }


        // Find the optimal route
        optimalAddress = RouteGeneration.OptimalRoute(testAddress);
        // Checking the routing part between two points with osmbonus
        // Needed to be executed in a separate thread due to Network activity
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        GeoPoint startPoint;
        GeoPoint endPoint;

        if(flag){
            for (int i = 0; i < optimalAddress.size(); i++){

                if((i + 1) == optimalAddress.size()){
                    break;
                }
                else{
                    startPoint = new GeoPoint(optimalAddress.get(i).getLatitude(),optimalAddress.get(i).getLongitude());
                    endPoint = new GeoPoint(optimalAddress.get(i+1).getLatitude(),optimalAddress.get(i+1).getLongitude());

                    waypoints.add(startPoint);
                    waypoints.add(endPoint);

                    new RoadMap().execute(waypoints);
                }
            }
        }
        else{
            textView.setText("No route available");
        }
        Log.v("Main activity","roadmanager test 1");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
//                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uppsalaMapView(){

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(59.8586, 17.6389);
        mapController.setCenter(startPoint);


    }

    private boolean settingMarkerinMap(){

        ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

        // Check if there is at least 2 entry data in the database to make the route
        if(MainActivity.count > 1){

            for(int i = 0; i < address.size() ; i++){
                overlayItems.add(new OverlayItem("","Testing Location",
                        new GeoPoint(address.get(i).getLatitude(), address.get(i).getLongitude())));
            }

            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay
                    = new ItemizedIconOverlay<OverlayItem>(this, overlayItems, null);
            mapView.getOverlays().add(anotherItemizedIconOverlay);
            return true;
        }
        else{
            return false;
        }
    }

    private void scaleBar(){
        //Add Scale Bar
        ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(mapView);
        mapView.getOverlays().add(myScaleBarOverlay);
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private class RoadMap extends AsyncTask<ArrayList<GeoPoint>,Integer,Polyline> {

        @Override
        protected Polyline doInBackground(ArrayList<GeoPoint>... waypoints) {

            RoadManager roadManager = new OSRMRoadManager(getApplicationContext());

            Road road = roadManager.getRoad(waypoints[0]);
            double mDuration = road.mDuration;

            Log.e(GeocodeConstants.TAG_ROUTE,"Duration : " + mDuration);
            Log.e(GeocodeConstants.TAG_ROUTE,"Length : " + road.mLength);

            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

            return  roadOverlay;
        }

        @Override
        protected void onPostExecute(Polyline result){
            mapView.getOverlays().add(result);
            mapView.invalidate();
        }
    }


}
