package com.mill_e.ustwo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalendarDBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "usTwoDatabase.db";
    public static final String EVENTS_DATABASE_TABLE = "Events";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_EVENT_ID = "EVENT_ID";
    public static final String KEY_EVENT_NAME = "EVENT_NAME";
    public static final String KEY_EVENT_LOCATION = "EVENT_LOCATION";
    public static final String KEY_EVENT_YEAR = "EVENT_YEAR";
    public static final String KEY_EVENT_MONTH = "EVENT_MONTH";
    public static final String KEY_EVENT_DAY = "EVENT_DAY";
    public static final String KEY_EVENT_HOUR = "EVENT_HOUR";
    public static final String KEY_EVENT_MINUTE = "EVENT_MINUTE";
    public static final String KEY_EVENT_REMINDER = "EVENT_REMINDER";


    private static final String DATABASE_CREATE = "create table if not exists " + EVENTS_DATABASE_TABLE + " (" + KEY_EVENT_ID + " integer primary key autoincrement, " +
            KEY_EVENT_NAME + " text not null, " + KEY_EVENT_LOCATION + " text, " + KEY_EVENT_YEAR + " integer not null, " + KEY_EVENT_MONTH + " integer not null, " + KEY_EVENT_DAY +
            " integer not null, " + KEY_EVENT_HOUR + " integer not null, " + KEY_EVENT_MINUTE + " integer not null, " + KEY_EVENT_REMINDER + " integer);";

    public CalendarDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        getWritableDatabase().execSQL(this.DATABASE_CREATE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(this.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
