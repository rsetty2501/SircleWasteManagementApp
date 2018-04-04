package com.example.android.routingwmsircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class SegmentedUsers extends AppCompatActivity{

    // Text view
    TextView displayView;
    ListView listView;

    // array list for user information
    ArrayList<UserInfoList> userInfoLists;
    UserInfoListAdapter userInfoListAdapter;
    String clusterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        displayView = findViewById(R.id.text_view_main);
        listView = findViewById(R.id.list_view_user);

        getClusterUserInfoList();
        setTitle(clusterName);
        // Setup FAB to open Route map
        FloatingActionButton mapFab = findViewById(R.id.map);
        mapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SegmentedUsers.this, RouteMap.class);
                intent.putExtra(GeocodeConstants.CLUSTER_NAME1,clusterName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected  void onStart(){
        super.onStart();
        getClusterUserInfoList();
    }

    private void getClusterUserInfoList(){
        userInfoLists = (ArrayList<UserInfoList>) getIntent().getSerializableExtra(GeocodeConstants.CLUSTER_USER_LIST);
        clusterName = getIntent().getStringExtra(GeocodeConstants.CLUSTER_NAME);
        userInfoListAdapter = new UserInfoListAdapter(getApplicationContext(), userInfoLists);
        listView.setAdapter(userInfoListAdapter);


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
}
