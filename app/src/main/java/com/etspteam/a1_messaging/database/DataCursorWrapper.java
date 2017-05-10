package com.etspteam.a1_messaging.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Pair;

public class DataCursorWrapper extends CursorWrapper {
    public DataCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Pair<String,String> getSettings() {
        String key = getString(getColumnIndex("key"));
        String value = getString(getColumnIndex("value"));
        return new Pair<>(key, value);
    }
}
