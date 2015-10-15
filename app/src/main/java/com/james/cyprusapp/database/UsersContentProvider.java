package com.james.cyprusapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by fappsilya on 13.10.15.
 */
public class UsersContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.james.cyprus";
    private static final String TABLE_NAME = "users";

    public static final Uri CONTENT_ADDRESS_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    private static final int ADDRESSES = 1;

    private static UriMatcher sUriMatcher;

    private UsersDataBase mDbHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, ADDRESSES);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new UsersDataBase(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = null;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case ADDRESSES:
                c = db.query(UsersDataBase.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri resultUri = null;
        switch (sUriMatcher.match(uri)) {
            case ADDRESSES:
                final long addressId = db.insertWithOnConflict(UsersDataBase.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                resultUri = ContentUris.withAppendedId(uri, addressId);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String tablename = null;
        switch (sUriMatcher.match(uri)) {
            case ADDRESSES:
                tablename = UsersDataBase.TABLE_NAME;
                break;
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();

        int counter = 0;
        for (ContentValues cv : values) {
            db.insertWithOnConflict(tablename, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            counter++;
            if (counter % 4 == 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        getContext().getContentResolver().notifyChange(uri, null);

        return values.length;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int deleted;

        switch (sUriMatcher.match(uri)) {
            case ADDRESSES:
                deleted = db.delete(UsersDataBase.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                deleted = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int updated;

        switch (sUriMatcher.match(uri)) {
            case ADDRESSES:
                updated = db.updateWithOnConflict(UsersDataBase.TABLE_NAME, values, selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
                break;
            default:
                updated = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return updated;
    }
}