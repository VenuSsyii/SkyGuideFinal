package com.lycha.assignment.skyguide;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;

import com.lycha.assignment.skyguide.LocationInfo.Location;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lycha.assignment.skyguide.LocationInfo.Location.COLUMN_NAME;
import static com.lycha.assignment.skyguide.LocationInfo.Location.COLUMN_DESCRIPTION;
import static com.lycha.assignment.skyguide.LocationInfo.Location.COLUMN_LATITUDE;
import static com.lycha.assignment.skyguide.LocationInfo.Location.COLUMN_LONGTITUDE;
import static com.lycha.assignment.skyguide.LocationInfo.Location.COLUMN_CATEGORIES;
import static com.lycha.assignment.skyguide.LocationInfo.Location.TABLE_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "location.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + Location.TABLE_NAME + "(" + Location.COLUMN_NAME + " TEXT," + Location.COLUMN_DESCRIPTION + " TEXT," + Location.COLUMN_LATITUDE + " REAL," + Location.COLUMN_LONGTITUDE + " REAL," + Location.COLUMN_CATEGORIES + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Location.TABLE_NAME;
    private String[] allColumn = {
            Location.COLUMN_NAME,
            Location.COLUMN_DESCRIPTION,
            Location.COLUMN_LATITUDE,
            Location.COLUMN_LONGTITUDE,
            Location.COLUMN_CATEGORIES
    };
    private SimpleDateFormat dateFormat;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insertRecord(AugmentedPOI poi){
        //Prepare Record
        ContentValues values = new ContentValues();
        values.put(Location.COLUMN_NAME, poi.getPoiName());
        values.put(Location.COLUMN_DESCRIPTION, poi.getPoiDescription());
        values.put(Location.COLUMN_LATITUDE, poi.getPoiLatitude());
        values.put(Location.COLUMN_LONGTITUDE, poi.getPoiLongitude());
        values.put(Location.COLUMN_CATEGORIES, poi.getmCategories());

        SQLiteDatabase database =  this.getWritableDatabase();
        database.insert(Location.TABLE_NAME, null, values);
        database.close();
    }

    public List<AugmentedPOI> getAllRecords(){
        List<AugmentedPOI> poiList = new ArrayList<AugmentedPOI>();
        SQLiteDatabase database = this.getReadableDatabase();
        double longt;

        Cursor cursor = database.query(Location.TABLE_NAME, allColumn, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            AugmentedPOI poi = new AugmentedPOI();
            poi.setPoiName(cursor.getString(0));
            poi.setPoiDescription(cursor.getString(1));
            poi.setPoiLatitude(cursor.getDouble(2));
            poi.setPoiLongitude(cursor.getDouble(3));
            poi.setmCategories(cursor.getString(4));
            poiList.add(poi);
            cursor.moveToNext();
        }
        cursor.close();
        return  poiList;
    }

    public List<String> getCategories(){
        List<String> catList = new ArrayList<>();
        catList.add("All");
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT DISTINCT categories FROM location",new String[]{});
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            catList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return  catList;
    }
}
