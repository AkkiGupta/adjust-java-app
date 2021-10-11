package com.adjust.task.cp;

import android.net.Uri;
import android.provider.BaseColumns;


public class ValueContract {
    public interface ValuesColumns {
        String VALUE_DATA = "value_data";
        String VALUE_STATUS = "value_status";
        String VALUE_CREATED_AT = "value_created_at";
    }

    // Used to access the content
    public static final String CONTENT_AUTHORITY = "com.adjust.task.cp.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_VALUES = "values";

    public static final Uri URI_TABLE = Uri.parse(BASE_CONTENT_URI.toString() + "/" + PATH_VALUES);

    public static final String[] TOP_LEVEL_PATHS = {PATH_VALUES};

    // Table for values
    public static class Values implements ValuesColumns, BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_VALUES).build();

        // Accessing content directory and item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".values";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".values";

        public static Uri buildValuesUri(String valuesId) {
            return CONTENT_URI.buildUpon().appendEncodedPath(valuesId).build();
        }

        public static String getValuesId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
