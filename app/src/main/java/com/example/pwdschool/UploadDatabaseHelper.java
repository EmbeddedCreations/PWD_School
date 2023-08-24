package com.example.pwdschool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UploadDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_UPLOAD = "uploads";
    public static final String COLUMN_SCHOOL_NAME = "school_name";
    public static final String COLUMN_PO_OFFICE = "po_office";
    public static final String COLUMN_ATC_OFFICE = "atc_office";
    public static final String COLUMN_JE = "junior_engg";
    public static final String COLUMN_VISIT_TYPE = "visit_type";
    public static final String COLUMN_BUILDING_NAME = "building_name";
    public static final String COLUMN_DATE_ADDED = "date_added";
    public static final String COLUMN_DATE_EXCIF = "date_excif";
    public static final String COLUMN_TIME_EXCIF = "time_excif";
    public static final String COLUMN_LATI = "lati";
    public static final String COLUMN_LONGI = "longi";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_IMG = "upload_img";
    private static final String DATABASE_NAME = "upload_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_ID = "id";
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_UPLOAD + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SCHOOL_NAME + " TEXT, " +
                    COLUMN_PO_OFFICE + " TEXT, " +
                    COLUMN_ATC_OFFICE + " TEXT, " +
                    COLUMN_JE + " TEXT, " +
                    COLUMN_VISIT_TYPE + " TEXT, " +
                    COLUMN_BUILDING_NAME + " TEXT, " +
                    COLUMN_DATE_ADDED + " TEXT, " +
                    COLUMN_DATE_EXCIF + " TEXT, " +
                    COLUMN_TIME_EXCIF + " TEXT, " +
                    COLUMN_LATI + " TEXT, " +
                    COLUMN_LONGI + " TEXT, " +
                    COLUMN_DESC + " TEXT, " +
                    COLUMN_TAGS + " TEXT, " +
                    COLUMN_IMG + " TEXT" +
                    ");";


    public UploadDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPLOAD);
        onCreate(db);
    }
}
