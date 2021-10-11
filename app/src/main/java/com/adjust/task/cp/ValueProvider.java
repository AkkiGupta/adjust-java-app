package com.adjust.task.cp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;



public class ValueProvider extends ContentProvider {
    private ValueDatabase mOpenHelper;

    private static String TAG = ValueProvider.class.getSimpleName();

    // Checks for valid URIs
    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static final int VALUE = 100;
    private static final int VALUE_ID = 101;

    private static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ValueContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "values", VALUE);
        matcher.addURI(authority, "values/#", VALUE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ValueDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case VALUE:
                return ValueContract.Values.CONTENT_TYPE;
            case VALUE_ID:
                return ValueContract.Values.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }
    }

    private void deleteDatabase(){
        mOpenHelper.close();
        ValueDatabase.deleteDatabse(getContext());
        mOpenHelper = new ValueDatabase(getContext());
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG,"insert(uri="+ uri + ", values="+values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case VALUE:
                // Create a new record
                long recordId = db.insertWithOnConflict(
                        ValueDatabase.Tables.VALUES,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_IGNORE);
                if(recordId == -1){
                    Log.e(TAG,"Duplicate Data for Seconds");
                } else {
                    this.getContext().getContentResolver().notifyChange(ValueContract.URI_TABLE, null);
                }
                return ValueContract.Values.buildValuesUri(String.valueOf(recordId));
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.v(TAG,"delete(uri="+ uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch(match){
            case VALUE:
                // Do nothing
                break;
            case VALUE_ID:
                String id = ValueContract.Values.getValuesId(uri);
                String selectionCriteria = BaseColumns._ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                return db.delete(ValueDatabase.Tables.VALUES, selectionCriteria, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri);
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG,"update(uri="+ uri + ", values="+values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String selectionCriteria = selection;

        switch (match){
            case VALUE:
                // Do nothing
                break;
            case VALUE_ID:
                String id = ValueContract.Values.getValuesId(uri);
                selectionCriteria = ValueContract.ValuesColumns.VALUE_DATA + "=" + id
                            + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }

        int updateCount = db.update(ValueDatabase.Tables.VALUES, values, selectionCriteria, selectionArgs);
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(ValueDatabase.Tables.VALUES);

        switch(match){
            case VALUE:
                break;
            case VALUE_ID:
                String id  = ValueContract.Values.getValuesId(uri);
                queryBuilder.appendWhere(ValueContract.ValuesColumns.VALUE_DATA + "="+ id);
                queryBuilder.appendWhere(ValueContract.ValuesColumns.VALUE_STATUS + "=pending");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI : "+ uri);
        }

        // Projection : Columns to return
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }
}
