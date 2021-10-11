package com.adjust.task.state_machine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.adjust.task.model.Value;
import com.adjust.task.cp.ValueContract;

class DataVerificationState extends State {

    private Context mContext;
    StateCallBackListener mListener;

    private DataVerificationState() {
    }

    public DataVerificationState(Context context, StateCallBackListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void startProcessing() {
        System.out.println("DataVerificationState::startProcessing");

        Value val_data = getData();
        if (val_data != null) {
            mListener.onDataAvailable(val_data);
        } else {
            mListener.onNoMoreDataAvailable();
        }
    }

    public void checkForData() {
        System.out.println("DataVerificationState::checkForMoreData");
        Value val_data = getData();
        if (val_data != null) {
            mListener.onDataAvailable(val_data);
        } else {
            mListener.onNoMoreDataAvailable();
        }
    }

    public void dataAvailable(ValueContract.Values val) {
        System.out.println("DataVerificationState::dataAvailable");
    }

    public void noMoreDataAvailable() {
        System.out.println("DataVerificationState::noMoreDataAvailable No Action Needed");
    }

    private Value getData() {
        Uri uri = ValueContract.URI_TABLE;
        String[] projection = new String[]{ValueContract.Values.VALUE_DATA,
                ValueContract.Values.VALUE_STATUS,
                ValueContract.Values.VALUE_CREATED_AT};
        String where = ValueContract.ValuesColumns.VALUE_STATUS+" = ?";
        String[] whereArgs = new String[]{"pending"};
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
}