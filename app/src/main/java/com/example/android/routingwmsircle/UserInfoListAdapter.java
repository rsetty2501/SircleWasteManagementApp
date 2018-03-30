package com.example.android.routingwmsircle;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.graphics.drawable.GradientDrawable;

/**
 * Created by Rahul Setty on 2/22/2018.
 */

public class UserInfoListAdapter  extends ArrayAdapter<UserInfoList>{

    private Context mContext;
    private List<UserInfoList> userList = new ArrayList<>();


    UserInfoListAdapter(@NonNull Context context, ArrayList<UserInfoList> objects) {
        super(context, 0, objects);
        mContext = context;
        userList = objects;
    }

    // The listView is the caller in this case to the getView() method
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.userinfo_list_item,parent,false);

        UserInfoList currentUser = userList.get(position);

        // To fetch the first letter from user's name and set it to the drawable circle
        TextView userNamefirstLetter = listItem.findViewById(R.id.username_firstLetter);
        userNamefirstLetter.setText(currentUser.getName().substring(0,1));
        // In order to set the background color, get the current background color and set it with appropriate one
        GradientDrawable gradientDrawable = (GradientDrawable) userNamefirstLetter.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int userColor = getUserColor(currentUser.getName());

        // Set the color on the magnitude circle
        gradientDrawable.setColor(userColor);

        // User name
        TextView userName = listItem.findViewById(R.id.textview_name);
        userName.setText(currentUser.getName());
        Log.v(GeocodeConstants.TAG_MAIN,"name:" + currentUser.getName());


        // User address
        TextView userAdd = listItem.findViewById(R.id.textView_address);
        userAdd.setText(currentUser.getAddress());
        Log.v(GeocodeConstants.TAG_MAIN,"address:" + currentUser.getAddress());

        // Latitude of the user's address
        TextView userLat = listItem.findViewById(R.id.textView_latitude);
        userLat.setText(formatCoordinates(currentUser.getAddress_latitude()));

        // Longitude of the user's address
        TextView userLong = listItem.findViewById(R.id.textView_longitude);
        userLong.setText(formatCoordinates(currentUser.getAddress_longitude()));

        return listItem;
    }

    private String formatCoordinates(double coordinates) {
        DecimalFormat locationFormat = new DecimalFormat("0.0000");
        return locationFormat.format(coordinates);
    }

    private int getUserColor(String userName){

        int userColorResourceId;
        char firstLetter = userName.toLowerCase().charAt(0);
        int ascii = (int) firstLetter;


        if(ascii >= 97 && ascii <= 99){
            userColorResourceId = R.color.alphabet1;
        }
        else
        if(ascii >= 100 && ascii <= 102){
            userColorResourceId = R.color.alphabet2;
        }
        else
        if(ascii >= 103 && ascii <= 105){
            userColorResourceId = R.color.alphabet3;
        }
        else
        if(ascii >= 106 && ascii <= 108){
            userColorResourceId = R.color.alphabet4;
        }
        else
        if(ascii >= 109 && ascii <= 111){
            userColorResourceId = R.color.alphabet5;
        }
        else
        if(ascii >= 112 && ascii <= 114){
            userColorResourceId = R.color.alphabet6;
        }
        else
        if(ascii >= 115 && ascii <= 117){
            userColorResourceId = R.color.alphabet7;
        }
        else
        if(ascii >= 118 && ascii <= 120){
            userColorResourceId = R.color.alphabet8;
        }
        else{
            userColorResourceId = R.color.alphabet9;
        }

        return  ContextCompat.getColor(getContext(), userColorResourceId);
    }
}
