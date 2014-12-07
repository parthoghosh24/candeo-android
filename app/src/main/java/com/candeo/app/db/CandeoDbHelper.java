package com.candeo.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Partho on 7/12/14.
 */
public class CandeoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="candeo.db";
    private static final String TABLE_NAME="users";
    private static final int DATABASE_VERSION=1;
    private static final String USER_ID ="_id";
    private static final String USER_NAME="name";
    private static final String USER_EMAIL="email";
    private static final String USER_USERNAME="username";
    private static final String USER_AUTH_TOKEN="auth_token";
    private static final String USER_UUID="uuid";
    private static final String CREATE_USER_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_NAME+" TEXT, "+USER_EMAIL+" TEXT, "+USER_USERNAME+" TEXT, "+USER_AUTH_TOKEN+" TEXT, "+USER_UUID+" TEXT);";




    public CandeoDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB created "+db);
        System.out.println("TABLE QUERY "+CREATE_USER_TABLE);
        try
        {
            db.execSQL(CREATE_USER_TABLE);
        }
        catch (SQLException sqe)
        {
            sqe.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
        onCreate(db);
    }


}
