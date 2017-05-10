package com.etspteam.a1_messaging.chat_room;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class MessagesDatabaseHelper extends SQLiteOpenHelper {

    public MessagesDatabaseHelper(Context context) {
        super(context, "messages", null, 1);
    }

    public void createTable(SQLiteDatabase db, String tableName) {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + tableName + " (\"saved\" TEXT, \"content\" TEXT, \"sender\" TEXT, \"time\" TEXT, \"state\" TEXT, \"type\" TEXT)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteallDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();
        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }
        c.close();
        for (String table : tables) {
            String dropQuery = "DELETE FROM " + table;
            db.execSQL(dropQuery);
        }
    }
}
