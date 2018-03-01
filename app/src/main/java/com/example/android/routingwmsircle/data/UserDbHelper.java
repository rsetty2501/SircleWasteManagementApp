package com.example.android.routingwmsircle.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rahul Setty on 2/20/2018.
 */

public class UserDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "userinfo.db";

    // Define constant for create table
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + UserInfo.UserEntry.TABLE_NAME
            + " ( " + UserInfo.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserInfo.UserEntry.COLUMN_USER_NAME + " TEXT NOT NULL, "
            + UserInfo.UserEntry.COLUMN_USER_ADDRESS + " TEXT NOT NULL, "
            + UserInfo.UserEntry.COLUMN_USER_REPLY + " INTEGER NOT NULL DEFAULT 0);";

    // Define constant for delete statement
    public static final String SQL_DELETE_TABLE = "DELETE FROM " + UserInfo.UserEntry.TABLE_NAME;

    public UserDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
