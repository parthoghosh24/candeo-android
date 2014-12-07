package com.candeo.app.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.candeo.app.CandeoApplication;

import java.util.HashMap;

/**
 * Created by Partho on 7/12/14.
 * This class will hold all the information related to owner of device.
 */
public class User {
    public long _id;
    public String name;
    public String username;
    public String email;
    public String authToken;
    public String uuid;

    private static final String TABLE_NAME="users";
    private static final String USER_ID ="_id";
    private static final String USER_NAME="name";
    private static final String USER_EMAIL="email";
    private static final String USER_USERNAME="username";
    private static final String USER_AUTH_TOKEN="auth_token";
    private static final String USER_UUID="uuid";

    public User(String name, String username, String email, String authToken, String uuid)
    {
        this.name = name;
        this.username = username;
        this.email = email;
        this.authToken = authToken;
        this.uuid = uuid;
    }
    public User()
    {}

    public static User findByUsername(String username)
    {
        User user =null;
        SQLiteDatabase db = CandeoApplication.getInstance().getDatabase().getReadableDatabase();
        String query ="SELECT * FROM "+TABLE_NAME+" WHERE username='"+username+"';";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor !=null)
        {
            cursor.moveToFirst();
            user = new User();
            user._id=cursor.getInt(cursor.getColumnIndex(USER_ID));
            user.name=cursor.getString(cursor.getColumnIndex(USER_NAME));
            user.username=cursor.getString(cursor.getColumnIndex(USER_USERNAME));
            user.email=cursor.getString(cursor.getColumnIndex(USER_EMAIL));
            user.authToken=cursor.getString(cursor.getColumnIndex(USER_AUTH_TOKEN));
            user.uuid=cursor.getString(cursor.getColumnIndex(USER_UUID));
            cursor.close();

        }

        return user;
    }

    public static User find(long id)
    {
        User user =null;
        SQLiteDatabase db = CandeoApplication.getInstance().getDatabase().getReadableDatabase();
        System.out.println("DB IS "+db.getPath());
        String query ="SELECT * FROM "+TABLE_NAME+" WHERE _id="+id;
        Cursor cursor = db.rawQuery(query,null);
        System.out.println("USER count is "+ cursor.getCount());

        if(cursor !=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            user = new User();
            user._id=cursor.getLong(cursor.getColumnIndex(USER_ID));
            user.name=cursor.getString(cursor.getColumnIndex(USER_NAME));
            user.username=cursor.getString(cursor.getColumnIndex(USER_USERNAME));
            user.email=cursor.getString(cursor.getColumnIndex(USER_EMAIL));
            user.authToken=cursor.getString(cursor.getColumnIndex(USER_AUTH_TOKEN));
            user.uuid=cursor.getString(cursor.getColumnIndex(USER_UUID));

        }
        return user;
    }

    public void update(HashMap<String, String> userMap)
    {

        SQLiteDatabase db = CandeoApplication.getInstance().getDatabase().getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+"se");

    }

    @Override
    public String toString() {

        return username;
    }
}
