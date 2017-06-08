package com.etspteam.a1_messaging.main.group;

import android.database.Cursor;
import android.database.CursorWrapper;

public class GroupMessageCursorWrapper extends CursorWrapper {
    public GroupMessageCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GroupMessagesListAdapter.Message getMessage(String user_name) {
        String from = getString(getColumnIndex("sender"));
        String content = getString(getColumnIndex("content"));
        String date = getString(getColumnIndex("time"));
        String type = getString(getColumnIndex("type"));
        String state = getString(getColumnIndex("state"));
        return new GroupMessagesListAdapter.Message(from, content, from.equals(user_name), date, state, type);
    }
}
