package com.james.cyprusapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fappsilya on 13.10.15.
 */
public class MyDBHElper extends SQLiteOpenHelper {
    private static final String DB_NAME = "users_db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "users";

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_AGE = "age";

    public static final String COLUMN_PHOTO = "photo";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " ( "
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_AGE + " INTEGER, "
            + COLUMN_PHOTO + " TEXT"
            + " );";

    public MyDBHElper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
