package com.adjust.task;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import com.adjust.task.cp.ValueContract;
import com.adjust.task.model.Value;

import java.util.ArrayList;
import java.util.List;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ValuesListLoader extends AsyncTaskLoader<List<Value>> {
    private static final String LOG_TAG = ValuesListLoader.class.getSimpleName();
    private List<Value> mValue;
    private ContentResolver mContentResolver;
    private Cursor mCursor;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ValuesListLoader(Context context, Uri uri, ContentResolver contentResolver) {
        super(context);
        mContentResolver = contentResolver;
    }

    @Override
    public List<Value> loadInBackground() {
        String[] whereArgs = new String[]{"pending"};
        String[] projection = {
                ValueContract.ValuesColumns.VALUE_DATA,
                ValueContract.ValuesColumns.VALUE_STATUS,
                ValueContract.ValuesColumns.VALUE_CREATED_AT,};

        List<Value> entries = new ArrayList<Value>();

        mCursor = mContentResolver.query(ValueContract.URI_TABLE, projection, ValueContract.ValuesColumns.VALUE_STATUS+" = ?", whereArgs, ValueContract.ValuesColumns.VALUE_CREATED_AT + " limit 1");

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                int value_sec = mCursor.getInt(mCursor.getColumnIndex(ValueContract.ValuesColumns.VALUE_DATA));
                String value_status = mCursor.getString(mCursor.getColumnIndex(ValueContract.ValuesColumns.VALUE_STATUS));
                long value_created_at = mCursor.getLong(mCursor.getColumnIndex(ValueContract.ValuesColumns.VALUE_CREATED_AT));
                Value val_sec = new Value(value_sec, value_created_at, value_status);
                entries.add(val_sec);
            }
        }
        return entries;
    }

    @Override
    public void deliverResult(List<Value> val_sec) {

        if (isReset()) {
            if (val_sec != null) {
                mCursor.close();
            }
        }

        List<Value> oldValSecList = mValue;

        if (mValue == null || mValue.size() == 0) {
            Log.d(LOG_TAG, "No Data Available ");
        }

        mValue = val_sec;

        if (isStarted()) {
            super.deliverResult(val_sec);
        }

        if (oldValSecList != null && oldValSecList != val_sec) {
            mCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mValue != null) {
            deliverResult(mValue);
        }

        if (takeContentChanged() || mValue == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mCursor != null) {
            mCursor.close();
        }

        mValue = null;
    }

    @Override
    public void onCanceled(List<Value> val_sec) {
        super.onCanceled(val_sec);
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }
}
