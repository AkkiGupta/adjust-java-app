package com.adjust.task.cp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ValueDatabase extends SQLiteOpenHelper {
    private static final String TAG = ValueDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "value.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    interface Tables {
        String VALUES = "value";
    }

    public ValueDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a SQLite DB on create
        db.execSQL("CREATE TABLE " + Tables.VALUES + " (" +
                ValueContract.ValuesColumns.VALUE_DATA + " INTEGER PRIMARY KEY, "+
                ValueContract.ValuesColumns.VALUE_STATUS + " TEXT NOT NULL, "+
                ValueContract.ValuesColumns.VALUE_CREATED_AT + " LONG NOT NULL);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void deleteDatabse(Context context) {

    }
}
