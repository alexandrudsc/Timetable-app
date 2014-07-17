package com.alexandru.developer.aplicatie_studenti.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import com.alexandru.developer.aplicatie_studenti.view_pager.Course;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

/**
 * Created by Alexandru on 7/6/14.
 */
public class DBAdapter {

    public static final String DB_NAME ="courses.db";
    public static int DB_VER = 1;

    //Tables
    public static final String COURSES_TABLE = "courses_table";

    //Columns of courses' table
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String FULL_NAME = "full_name";
    public static final String TYPE = "type";
    public static final String LOCATION = "location";
    public static final String TIME = "time";
    public static final String DAY = "day";
    public static final String PROF = "prof";
    public static final String INFO = "info";


    //SQLite statements for courses table
    private static final String TYPE_TEXT_NOT_NULL = " TEXT NOT NULL";
    private static final String COMMA  = ",";
    private static final String CREATE_COURSES_TABLE = "CREATE TABLE " + COURSES_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY," +
            NAME + TYPE_TEXT_NOT_NULL + COMMA +
            FULL_NAME + " TEXT" + COMMA +
            TYPE + " TEXT," +
            LOCATION + TYPE_TEXT_NOT_NULL + COMMA +
            TIME + TYPE_TEXT_NOT_NULL + COMMA +
            DAY + TYPE_TEXT_NOT_NULL + COMMA +
            PROF + " TEXT,"+
            INFO + TYPE_TEXT_NOT_NULL + " )";
    private static final String DELETE_COURSES_TABLE = "DROP TABLE IF EXISTS "+ COURSES_TABLE;

    private DBOpenHelper dbHelper;
    private SQLiteDatabase database;

    public DBAdapter(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    public void open() throws SQLiteException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(database.isOpen())
            database.close();
    }

    public long insertCourse(Course course, String day){

        ContentValues values = new ContentValues();
        values.put(NAME, course.name);
        values.put(FULL_NAME, course.fullName);
        values.put(TYPE, course.type);
        values.put(LOCATION, course.location);
        values.put(TIME, course.time);
        values.put(DAY, day);
        values.put(PROF, course.prof);
        values.put(INFO, course.info);
        return database.insert(COURSES_TABLE, null, values);
    }

    public ArrayList<Course> getCourses(int week, String day){
        String[] mProjection={ "name",
                               "full_name",
                               "type",
                               "location",
                               "time",
                               "prof",
                               "info"
        };

        String selection ="day == ? AND (info == ? OR info == ?)";
        String[] selectionArgs = new String[3];
        String orderBy = "time";
        selectionArgs[0] = day;
        selectionArgs[2] = "";
        if(week % 2 == 0)
            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_EVEN_WEEK;
        else
            selectionArgs[1] = ViewPagerAdapter.COURSES_IN_ODD_WEEK;

        Cursor cursor = database.query(COURSES_TABLE,
                                       mProjection,
                                       selection,
                                       selectionArgs,
                                       null,
                                       null,
                                       orderBy);
        if(cursor == null)
            return null;
        ArrayList<Course> courses = new ArrayList<Course>();
        String name, fullName, type, time, info, location, prof;
        while(cursor.moveToNext()){
            name = cursor.getString(0);
            fullName = cursor.getString(1);
            type = cursor.getString(2);
            location = cursor.getString(3);
            time = cursor.getString(4);
            prof = cursor.getString(5);
            info = cursor.getString(6);

            courses.add(new Course(name, fullName, type, location, time, prof, info));

        }
        return courses;
    }

    public Cursor fullQuery(String[] projection, String selection, String[] selectionArgs, String order){
        // Used by content provider for search suggestions
        Log.d("DBAdapter", "query for search");
        Log.d("DBAdapter", selection + " " + selectionArgs[0] + " " + order);
        String[] selArgs = new String[]{selectionArgs[0]+"%"};
        return database.query(COURSES_TABLE,
                            projection,
                            selection,
                            selArgs,
                            null,
                            null,
                            order);
    }

    public String[] getInfoAboutCourse(String name, String type){
        //Used by SearchableActivity when searching was made specifically
        String[] projection={ "name",
                            "full_name",
                            "type",
                            "location",
                            "time",
                            "prof",
                            "info"
        };
        String selection = "LOWER(name) == ? AND LOWER(type) == ?";
        String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
        Cursor result = database.query(COURSES_TABLE,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        null);
        if(result == null || result.getCount() == 0)
            return null;
        result.moveToFirst();
        return new String[]{result.getString(0), result.getString(1), result.getString(2)};
    }

    public Course getCourse(String name, String type){
        //Used by SearchableActivity when searching was made specifically

        String[] mProjection={ "name",
                "full_name",
                "type",
                "location",
                "time",
                "prof",
                "info"
        };

        String selection = "LOWER(name) == ? AND LOWER(type) == ?";
        String[] selectionArgs = {name.toLowerCase(), type.toLowerCase()};
        Cursor result = database.query(COURSES_TABLE,
                mProjection,
                selection,
                selectionArgs,
                null,
                null,
                null);
        if(result == null || result.getCount() == 0)
            return null;
        result.moveToFirst();
        return  new Course(result.getString(0), result.getString(1), result.getString(2),
                            result.getString(3), result.getString(4), result.getString(5),
                            result.getString(6));

    }

    public boolean isOpen(){
        return database.isOpen();
    }

    private class DBOpenHelper extends SQLiteOpenHelper{

        public DBOpenHelper(Context context){
            super(context, DB_NAME, null, DB_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_COURSES_TABLE);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, newVersion, oldVersion);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            DB_VER = newVersion;

            sqLiteDatabase.execSQL(DELETE_COURSES_TABLE);

            onCreate(sqLiteDatabase);
        }
    }
}
