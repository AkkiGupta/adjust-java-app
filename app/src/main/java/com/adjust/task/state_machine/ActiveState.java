package com.adjust.task.state_machine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.adjust.task.model.Value;
import com.adjust.task.cp.ValueContract;
import com.adjust.task.network.PostSecondsJson;
import com.adjust.task.util.Constants;

class ActiveState extends State implements OnPostDone {
    StateCallBackListener mListener;
    private Context mContext;

    private ActiveState() {
    }

    public ActiveState(Context context, StateCallBackListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void startProcessing() {
        System.out.println("ActiveState::startProcessing No Action Needed");
    }

    public void checkForData() {
        System.out.println("ActiveState::checkForMoreData No Action Needed");
    }

    public void dataAvailable(Value val) {
        System.out.println("ActiveState::dataAvailable");

        // Perform Network call and update DB on success
        new PostSecondsJson(mContext, val.getValue_sec(), this).execute(Constants.URL);

    }

    public void noMoreDataAvailable() {
        System.out.println("ActiveState::noMoreDataAvailable");
    }

    private boolean updateDb(int val) {
        String where = null;//ValueContract.ValuesColumns.VALUE_DATA+" = ?";
        String[] whereArgs = null;//new String[]{""+val};

        ContentValues values = new ContentValues();
        values.put(ValueContract.ValuesColumns.VALUE_STATUS, "processed");

        Uri uri = ValueContract.Values.buildValuesUri("" + val);
        int returned = mContext.getContentResolver().update(
                uri,
                values,
                where,
                whereArgs);

        if (returned == 1) return true;
        else return false;

    }

    private Value getData() {
        Uri uri = ValueContract.URI_TABLE;
        String[] projection = new String[]{ValueContract.Values.VALUE_DATA,
                ValueContract.Values.VALUE_STATUS,
                ValueContract.Values.VALUE_CREATED_AT};
        String where = null;
        String[] whereArgs = null;
        String sortOrder = ValueContract.Values.VALUE_CREATED_AT
                + " limit 1";

        Cursor mCursor = mContext.getContentResolver().query(
                uri,
                projection,
                where,
                whereArgs,
                sortOrder
        );

        if (mCursor != null) {
            if (mCursor.moveToFirst()) {
                int value_sec = mCursor.getInt(mCursor.getColumnIndex(ValueContract.ValuesColumns.VALUE_DATA));
                String value_status = mCursor.getString(mCursor.getColumnIndex(ValueContract.ValuesColumns.VALUE_STATUS));
                long value_created_at = mCursor.getLong(mCursor.getColumnIndex(ValueContract.ValuesColumns.VALUE_CREATED_AT));
                Value val_sec = new Value(value_sec, value_created_at, value_status);
                return val_sec;
            }
        }

        return null;
    }

    @Override
    public void requestDone(Boolean success, String response, int value, int id) {
        Log.v("ActiveState", "" + value + "," + id);
        if (success && value != -1 && id != -1) {
            updateDb(value);
            mListener.onCheckForData();
        } else {
            mListener.onNoMoreDataAvailable();
        }
    }
}