package com.etspteam.a1_messaging.main.message;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.chat_room.MessagesDatabaseHelper;
import com.etspteam.a1_messaging.database.DataApplicationHelper;
import com.etspteam.a1_messaging.database.DataCursorWrapper;
import com.etspteam.a1_messaging.main.MainActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MessageFragment extends Fragment {
    private String userName;
    private List<List<String>> messageItems;
    private SQLiteDatabase database;
    private RecyclerView listMessageItems;
    private ChildEventListener listener;
    private DatabaseReference root_user_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        ((MainActivity) getActivity()).setToolbarAndStatusBarColor(R.color.blue, R.color.bluedark);
        SQLiteDatabase mDatabase = new DataApplicationHelper(getContext()).getWritableDatabase();
        Cursor cursor1 = mDatabase.query("settings", null, null, null, null, null, null);
        DataCursorWrapper cursor = new DataCursorWrapper(cursor1);
        Pair<String, String> pair;
        try {
            cursor.moveToFirst();
            pair = cursor.getSettings();
        } finally {
            cursor.close();
            cursor1.close();
        }
        userName = pair.second;

        database = new MessagesDatabaseHelper(getContext()).getWritableDatabase();
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tableName = new ArrayList<>();
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tableName.add(c.getString(0));
                c.moveToNext();
            }
        }
        c.close();

        if (userName.equals("")) return v;
        messageItems = new ArrayList<>();
        for (int i = 1; i < tableName.size(); i++) {
            List<String> tempList = new ArrayList<>();
            tempList.add(Integer.toString(getIndexMember(tableName.get(i))));
            Cursor cursor2 = database.query(tableName.get(i), null, null, null, null, null, null);
            cursor2.moveToLast();
            if (!cursor2.isBeforeFirst()) {
                String sender = cursor2.getString(cursor2.getColumnIndex("sender"));
                if (sender.equals(userName)) {
                    tempList.add("true");
                } else {
                    tempList.add("false");
                }
                tempList.add(cursor2.getString(cursor2.getColumnIndex("content")));
                tempList.add(cursor2.getString(cursor2.getColumnIndex("time")));
                tempList.add(cursor2.getString(cursor2.getColumnIndex("state")));
                tempList.add(cursor2.getString(cursor2.getColumnIndex("type")));
                messageItems.add(tempList);
            }
            cursor2.close();
        }

        root_user_name = FirebaseDatabase.getInstance().getReference().child("messages").child(userName);
        root_user_name.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update_new_messages(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_new_messages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listMessageItems = (RecyclerView) v.findViewById(R.id.list_message_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listMessageItems.setLayoutManager(layoutManager);
        listMessageItems.setHasFixedSize(true);
        listMessageItems.setAdapter(new MessageAdapter(getContext(), messageItems));
        return v;
    }

    private int getIndexMember(String dbName) {
        int index1 = Integer.parseInt(dbName.substring(2,4));
        int index2 = Integer.parseInt(dbName.substring(4));
        if (userName.equals(ListMember.getList().get(index1 - 1).shortname)) {
            return index2 - 1;
        }
        return index1 - 1;
    }

    private void update_new_messages(final DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String sender = (String) ((DataSnapshot) i.next()).getValue();
            DataSnapshot state = (DataSnapshot) i.next();
            String date = (String) ((DataSnapshot) i.next()).getValue();
            String type = (String) ((DataSnapshot) i.next()).getValue();

            List<String> tempList = new ArrayList<>();
            int indexSender = 0;
            for (int j = 0; j < ListMember.getList().size(); j++) {
                if (ListMember.getList().get(j).shortname.equals(sender)) {
                    indexSender = j;
                }
            }
            tempList.add(Integer.toString(indexSender));
            tempList.add("false");
            tempList.add(chat_msg);
            tempList.add(date);
            tempList.add("Đã nhận");
            tempList.add(type);
            if (state.getValue().equals("Đã gửi")) {
                int indexToRemove = -1;
                for (int j = 0; j < messageItems.size(); j++) {
                    if (messageItems.get(j).get(0).equals(Integer.toString(indexSender))) {
                        indexToRemove = j;
                        break;
                    }
                }
                if (indexToRemove >= 0) {
                    messageItems.remove(indexToRemove);
                    messageItems.add(tempList);
                }
                ContentValues values = new ContentValues();
                values.put("content", chat_msg);
                values.put("time", date);
                values.put("sender", sender);
                values.put("state", "Đã nhận");
                values.put("type", type);
                new MessagesDatabaseHelper(getContext()).createTable(database, getTableNameFromSenderName(sender,userName));
                database.insert(getTableNameFromSenderName(sender,userName), null, values);
                state.getRef().setValue("Đã nhận");
            }
        }
        listMessageItems.setAdapter(new MessageAdapter(getContext(), messageItems));
    }

    public static String getTableNameFromSenderName(String sender, String un) {
        int idUserName = 0, idSender = 0;
        for (ListMember.Member member : ListMember.getList()) {
            if (member.shortname.equals(sender)) idSender = member.id;
            if (member.shortname.equals(un)) idUserName = member.id;
        }
        String id_user_name;
        if (idUserName < 10) id_user_name = "0" + Integer.valueOf(idUserName).toString();
        else id_user_name = Integer.valueOf(idUserName).toString();

        String id_room_name;
        if (idSender < 10) id_room_name = "0" + Integer.valueOf(idSender).toString();
        else id_room_name = Integer.valueOf(idSender).toString();

        String chileName;
        if (idUserName < idSender) chileName = id_user_name + id_room_name;
        else chileName = id_room_name + id_user_name;
        return "db" + chileName;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userName.equals("") || root_user_name == null) return;
        listener = root_user_name.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update_new_messages(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_new_messages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (root_user_name == null || listener == null) return;
        root_user_name.removeEventListener(listener);
    }
}
