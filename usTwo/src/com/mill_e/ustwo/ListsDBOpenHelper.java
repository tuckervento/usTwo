package com.mill_e.ustwo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Custom SQLiteOpenHelper for the Lists database.
 */
public class ListsDBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "usTwoDatabase.db";
    public static final String LISTS_DATABASE_TABLE = "Lists";
    public static final int DATABASE_VERSION = 1;
    public static final String KEY_LIST_NAME = "LIST_NAME";
    public static final String KEY_LIST_ITEM = "LIST_ITEM";
    public static final String KEY_CHECKED = "CHECKED";

    private static final String DATABASE_CREATE = "create table if not exists " + LISTS_DATABASE_TABLE + " (" + KEY_LIST_NAME + " text not null, "
            + KEY_LIST_ITEM + " text not null, " + KEY_CHECKED + " integer not null);";

    public ListsDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        getWritableDatabase().execSQL(DATABASE_CREATE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
