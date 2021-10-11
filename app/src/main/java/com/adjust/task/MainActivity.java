package com.adjust.task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Loader;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.adjust.task.cp.ValueContract;
import com.adjust.task.model.Value;
import com.adjust.task.state_machine.Fsm;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Value>> {

    private static final String LOG_TAG = "MainActivity";

    private Button btnSubmit;
    private static final int LOADER_ID = 1;

    private ContentResolver mContentResolver;
    private Fsm fsmInstance;

    private ContentObserver cObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            fsmInstance.startProcessing();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fsmInstance = Fsm.getInstance(MainActivity.this);

        initContentProvider();
        initUi();
    }

    private void initUi() {
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCurrentTimeAndSubmit();
                    }
                }
        );
    }

    private void initContentProvider() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        getContentResolver().registerContentObserver(
                ValueContract.URI_TABLE,
                true,
                cObserver
        );
        mContentResolver = MainActivity.this.getContentResolver();
    }

    private void getCurrentTimeAndSubmit() {

        Calendar now = Calendar.getInstance();
        int second = now.get(Calendar.SECOND);

        ContentValues values = new ContentValues();
        values.put(ValueContract.ValuesColumns.VALUE_DATA, second);
        values.put(ValueContract.ValuesColumns.VALUE_STATUS, "pending");
        values.put(ValueContract.ValuesColumns.VALUE_CREATED_AT, System.currentTimeMillis());
        Uri returned = mContentResolver.insert(ValueContract.URI_TABLE, values);
    }

    @NonNull
    @Override
    public Loader<List<Value>> onCreateLoader(int id, @Nullable Bundle args) {
        mContentResolver = MainActivity.this.getContentResolver();
        return new ValuesListLoader(MainActivity.this, ValueContract.URI_TABLE, mContentResolver);
    }

    @Override
    public void onLoadFinished(Loader<List<Value>> loader, List<Value> values) {
        if(values.size() > 0) {
            fsmInstance.startProcessing();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Value>> loader) { }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(cObserver);
    }
}