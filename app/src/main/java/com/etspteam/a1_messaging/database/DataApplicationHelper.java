package com.etspteam.a1_messaging.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.util.Pair;

import com.etspteam.a1_messaging.main.contact.ListMember;

public class DataApplicationHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE \"settings\" (\"key\" TEXT, \"value\" TEXT)";

    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE \"favorite\" (\"indexMember\" TEXT, \"isFavorite\" TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + "settings";

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "settings.db";

    public DataApplicationHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES2);
        db.insert("settings", null, getContentValues(new Pair<>("user_name", "")));
        db.insert("settings", null, getContentValues(new Pair<>("keyboard_height", "0")));
        for (ListMember.Member member: ListMember.getList()) {
            ContentValues values = new ContentValues();
            values.put("indexMember", Integer.toString(member.id-1));
            values.put("isFavorite", "0");
            db.insert("favorite", null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private ContentValues getContentValues(Pair<String,String> pair) {
        ContentValues values = new ContentValues();
        values.put("key", pair.first);
        values.put("value", pair.second);
        return values;
    }
}
