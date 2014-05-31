package com.mill_e.ustwo.DataModel.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Custom SQLiteOpenHelper for the CalendarEvents database.
 */
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
    public static final String KEY_TIMESTAMP = "EVENT_TIMESTAMP";
    public static final String KEY_SENDER = "EVENT_SENDER";


    private static final String DATABASE_CREATE = "create table if not exists " + EVENTS_DATABASE_TABLE + " (" + KEY_EVENT_ID + " integer primary key autoincrement, " + KEY_TIMESTAMP + " integer not null,"
            + KEY_SENDER + " text not null, " + KEY_EVENT_NAME + " text not null, " + KEY_EVENT_LOCATION + " text, " + KEY_EVENT_YEAR + " integer not null, " + KEY_EVENT_MONTH + " integer not null, "
            + KEY_EVENT_DAY + " integer not null, " + KEY_EVENT_HOUR + " integer not null, " + KEY_EVENT_MINUTE + " integer not null, " + KEY_EVENT_REMINDER + " integer not null);";

    public CalendarDBOpenHelper(Context context){
        super(context, CalendarDBOpenHelper.DATABASE_NAME, null, CalendarDBOpenHelper.DATABASE_VERSION);
        try{
            getWritableDatabase().execSQL(DATABASE_CREATE);
        }catch(NullPointerException e){ e.printStackTrace(); }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
