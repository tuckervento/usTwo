package com.mill_e.ustwo.DataModel.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Custom SQLiteOpenHelper for the Messages database.
 */
public class MessagingDBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "usTwoDatabase.db";
    public static final String MESSAGE_DATABASE_TABLE = "Messages";
    public static final int DATABASE_VERSION = 1;
    @SuppressWarnings("WeakerAccess")
    public static final String KEY_MESSAGE_ID = "MESSAGE_ID";
    public static final String KEY_MESSAGE_CONTENTS = "MESSAGE_CONTENTS";
    public static final String KEY_MESSAGE_SENDER = "MESSAGE_SENDER";
    public static final String KEY_MESSAGE_TIMESTAMP = "MESSAGE_TIMESTAMP";
    public static final String KEY_MESSAGE_SYSTEM = "MESSAGE_SYSTEM";


    private static final String DATABASE_CREATE = "create table if not exists " + MESSAGE_DATABASE_TABLE + " (" + KEY_MESSAGE_ID + " integer primary key autoincrement, " +
            KEY_MESSAGE_SYSTEM + " integer not null, " + KEY_MESSAGE_TIMESTAMP + " integer not null, " + KEY_MESSAGE_SENDER + " text not null, " + KEY_MESSAGE_CONTENTS + " text);";

    public MessagingDBOpenHelper(Context context){
        super(context, MessagingDBOpenHelper.DATABASE_NAME, null, MessagingDBOpenHelper.DATABASE_VERSION);
        try{
            getWritableDatabase().execSQL(DATABASE_CREATE);
        }catch(NullPointerException e) { e.printStackTrace(); }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
