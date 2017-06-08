package com.etspteam.a1_messaging.chat_room;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageCursorWrapper extends CursorWrapper {
    public MessageCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public MessagesListAdapter.Message getMessage(String user_name) {
        String from = getString(getColumnIndex("sender"));
        String content = getString(getColumnIndex("content"));
        String date = getString(getColumnIndex("time"));
        String type = getString(getColumnIndex("type"));
//        DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        Date date1 = new Date();
//        try {
//            date1 = sdf.parse(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        String state = getString(getColumnIndex("state"));
        return new MessagesListAdapter.Message(from, content, from.equals(user_name), date, state, type);
    }
}
