package com.alexandru.developer.aplicatie_studenti.storage;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Alexandru on 7/5/14.
 */
public class CoursesContentProvider extends ContentProvider {

    private ArrayList<String> coursesName;
    DBAdapter dbAdapter;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void shutdown() {
        super.shutdown();
        if (dbAdapter.isOpen())
            dbAdapter.close();
    }

    @Override
    public boolean onCreate() {
        dbAdapter = new DBAdapter(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order)
                  throws IllegalArgumentException, NullPointerException{
        dbAdapter.open();
        Log.d("Content provider", uri.toString());
        Cursor cursor = null;
        //Used when search suggestions are needed.Columns returned multiple times for sending data through intent.
        projection = new String[]{"_id AS " + BaseColumns._ID,
                                  "name AS " + SearchManager.SUGGEST_COLUMN_TEXT_1,
                                  "type AS " + SearchManager.SUGGEST_COLUMN_TEXT_2,
                                  "name AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
                                  "type AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                                  "info AS " + SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA};
        selection = "name LIKE ?";
        order = "name, type";
        cursor = dbAdapter.fullQuery(projection, selection, selectionArgs, order);
        cursor.moveToFirst();

        Log.d("Content provider", cursor.getString(1));
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
