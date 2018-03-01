package com.example.android.routingwmsircle.data;

import android.provider.BaseColumns;

/**
 * Created by Rahul Setty on 2/20/2018.
 */

// Make the class final since we do not want to extend it
public final class UserInfo {

    private UserInfo(){

    }

    public static final class UserEntry implements BaseColumns{

        public static final String TABLE_NAME = "userinfo";

        // Define just the constants name, not the type of them
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USER_NAME = "name";
        public static final String COLUMN_USER_ADDRESS = "address";
        public static final String COLUMN_USER_REPLY = "reply";

        // Define constants for user reply
        public static final int YES = 1;
        public static final int NO = 0;
    }
}
