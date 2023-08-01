package com.example.pwdschool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String UPLOAD_TABLE = "upload";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SELECTED_SCHOOL = "selectedSchool";
    public static final String COLUMN_SELECTED_WORKORDER = "selectedWorkorder";
    public static final String COLUMN_SELECTED_BUILDING = "selectedBuilding";
    public static final String COLUMN_SELECTED_DATE = "selectedDate";

    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        // Create the User table
        DB.execSQL("CREATE TABLE User(name TEXT PRIMARY KEY, score TEXT)");

        // Create the Upload table
        DB.execSQL("CREATE TABLE " + UPLOAD_TABLE + " ("
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_SELECTED_SCHOOL + " TEXT,"
                + COLUMN_SELECTED_WORKORDER + " TEXT,"
                + COLUMN_SELECTED_BUILDING + " TEXT,"
                + COLUMN_SELECTED_DATE + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Method to insert data into the "upload" table
    public Boolean insertUploadData(String description, String selectedSchool, String selectedWorkorder,
                                    String selectedBuilding, String selectedDate) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DESCRIPTION, Upload.description);
        contentValues.put(COLUMN_SELECTED_SCHOOL, Home.selectedSchool);
        contentValues.put(COLUMN_SELECTED_WORKORDER, Home.selectedWorkorder);
        contentValues.put(COLUMN_SELECTED_BUILDING, Home.selectedBuilding);
        contentValues.put(COLUMN_SELECTED_DATE, Home.selectedDate);

        long result = DB.insert(UPLOAD_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // Method to display data from the "upload" table on the console
    public void displayUploadData() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM " + UPLOAD_TABLE, null);

        int columnIndexDescription = cursor.getColumnIndex(COLUMN_DESCRIPTION);
        int columnIndexSelectedSchool = cursor.getColumnIndex(COLUMN_SELECTED_SCHOOL);
        int columnIndexSelectedWorkorder = cursor.getColumnIndex(COLUMN_SELECTED_WORKORDER);
        int columnIndexSelectedBuilding = cursor.getColumnIndex(COLUMN_SELECTED_BUILDING);
        int columnIndexSelectedDate = cursor.getColumnIndex(COLUMN_SELECTED_DATE);

        if (cursor.moveToFirst()) {
            do {
                // Check if the columns exist in the cursor
                if (columnIndexDescription >= 0) {
                    String description = cursor.getString(columnIndexDescription);
                    System.out.println("Description: " + description);
                }

                if (columnIndexSelectedSchool >= 0) {
                    String selectedSchool = cursor.getString(columnIndexSelectedSchool);
                    System.out.println("Selected School: " + selectedSchool);
                }

                if (columnIndexSelectedWorkorder >= 0) {
                    String selectedWorkorder = cursor.getString(columnIndexSelectedWorkorder);
                    System.out.println("Selected Workorder: " + selectedWorkorder);
                }

                if (columnIndexSelectedBuilding >= 0) {
                    String selectedBuilding = cursor.getString(columnIndexSelectedBuilding);
                    System.out.println("Selected Building: " + selectedBuilding);
                }

                if (columnIndexSelectedDate >= 0) {
                    String selectedDate = cursor.getString(columnIndexSelectedDate);
                    System.out.println("Selected Date: " + selectedDate);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

}
