package com.etspteam.a1_messaging.chat_room;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.database.DataApplicationHelper;
import com.etspteam.a1_messaging.database.DataCursorWrapper;
import com.etspteam.a1_messaging.main.contact.ListMember;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements StickersGridAdapter.KeyClickListener, KeyboardHeightObserver {

    private EditText input_msg;
    private List<MessagesListAdapter.Message> listMessages;
    private String user_name;
    private DatabaseReference root_user_name;
    private DatabaseReference root_room_name;
    private int idUserName;
    private int idRoomName;
    private String room_name;
    private SQLiteDatabase database;
    private String tableName;
    private ChildEventListener listener_user;
    private ChildEventListener listener_room;
    private Handler handler;
    private View headerView;
    private boolean isLoading = false;
    private ListView listViewMessages;
    private MessageCursorWrapper cursor2;
    private boolean hasJustOpen = true;
    private View popupView;
    private PopupWindow popupWindow;
    private int keyboardHeight;
    private LinearLayout rootLayout;
    private InputMethodManager imm;
    private ImageView stickerSelection;
    private TabLayout tabSticker;
    private KeyboardHeightProvider keyboardHeightProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rootLayout = (LinearLayout) findViewById(R.id.chat_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.line_color)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.dark_line_color));
        }

        final ImageView btn_send_msg = (ImageView) findViewById(R.id.btn_send);
        input_msg = (EditText) findViewById(R.id.msg_input);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = li.inflate(R.layout.header_chat, null);
        popupView = getLayoutInflater().inflate(R.layout.sticker_popup, null);
        handler = new MessageHandler();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        tabSticker = (TabLayout) popupView.findViewById(R.id.tab_sticker);
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        rootLayout.post(new Runnable() {
            @Override
            public void run() {
                keyboardHeightProvider.start();
            }
        });

        SQLiteDatabase mDatabase = new DataApplicationHelper(this).getWritableDatabase();
        Cursor cursor1 = mDatabase.query("settings", null, null, null, null, null, null);
        DataCursorWrapper cursor = new DataCursorWrapper(cursor1);
        List<Pair<String,String>> listSettings = new ArrayList<>();
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listSettings.add(cursor.getSettings());
                cursor.moveToNext();
            }
        } finally {
            cursor1.close();
            cursor.close();
        }
        user_name = listSettings.get(0).second;

        int index = getIntent().getIntExtra("com.etspteam.index", 0);
        room_name = ListMember.getList().get(index).shortname;
        setTitle(ListMember.getList().get(index).name);

        idRoomName = ListMember.getList().get(index).id;
        idUserName = 0;
        for (ListMember.Member member : ListMember.getList()) {
            if (member.shortname.equals(user_name)) {
                idUserName = member.id;
            }
        }
        String chileName = getChildName();
        root_room_name = FirebaseDatabase.getInstance().getReference().child("messages").child(room_name);
        root_user_name = FirebaseDatabase.getInstance().getReference().child("messages").child(user_name);

        MessagesDatabaseHelper databaseHelper = new MessagesDatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        tableName = "db" + chileName;
        databaseHelper.createTable(database, tableName);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(input_msg.getText().toString(), "text");
            }
        });

        input_msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    input_msg.requestFocus();
                    imm.showSoftInput(input_msg, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        listMessages = new ArrayList<>();
        listMessages.add(new MessagesListAdapter.Message("", "", true, "", "", "text"));
        listMessages.add(new MessagesListAdapter.Message("", "", true, "", "", "text"));

        cursor2 = new MessageCursorWrapper(database.query(tableName, null, null, null, null, null, null));
        int count = 0;
        cursor2.moveToLast();
        while (count <= 15 && !cursor2.isBeforeFirst()) {
            listMessages.add(1, cursor2.getMessage(user_name));
            count++;
            cursor2.moveToPrevious();
        }

        MessagesListAdapter adapter = new MessagesListAdapter(this, listMessages, idUserName, idRoomName);
        listViewMessages.setAdapter(adapter);

        root_room_name.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

        listViewMessages.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > visibleItemCount && firstVisibleItem == 0 && !isLoading) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                }
            }
        });

        stickerSelection = (ImageView) findViewById(R.id.sticker_selection);

        keyboardHeight = Integer.parseInt(listSettings.get(1).second);
        stickerSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_msg.requestFocus();
                imm.showSoftInput(input_msg, InputMethodManager.SHOW_IMPLICIT);
                if (!popupWindow.isShowing()) {
                    if (keyboardHeight < 100) return;
                    stickerSelection.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard));
                    popupWindow.setHeight(keyboardHeight);
                    popupWindow.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
                } else {
                    stickerSelection.setImageDrawable(getResources().getDrawable(R.drawable.ic_sticker));
                    popupWindow.dismiss();
                }
            }
        });
        enablePopUpView();

        input_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    btn_send_msg.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                } else {
                    btn_send_msg.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static Intent newIntent(Context context, String rn) {
        Intent intent = new Intent(context, ChatActivity.class);
        int index = 0;
        for (int i = 0; i < ListMember.getList().size(); i++) {
            if (rn.equals(ListMember.getList().get(i).shortname)) {
                index = i;
                break;
            }
        }
        intent.putExtra("com.etspteam.index", index);
        return intent;
    }

    private String getChildName() {
        String id_user_name;
        if (idUserName < 10) id_user_name = "0" + Integer.valueOf(idUserName).toString();
        else id_user_name = Integer.valueOf(idUserName).toString();

        String id_room_name;
        if (idRoomName < 10) id_room_name = "0" + Integer.valueOf(idRoomName).toString();
        else id_room_name = Integer.valueOf(idRoomName).toString();

        if (idUserName < idRoomName) return id_user_name + id_room_name;
        else return id_room_name + id_user_name;
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if (height < 100) return;
        keyboardHeight = height - 1;
        SQLiteDatabase mDatabase = new DataApplicationHelper(getBaseContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("key", "keyboard_height");
        values.put("value", Integer.toString(keyboardHeight));
        mDatabase.update("settings", values, "key" + " = ?", new String[]{"keyboard_height"});
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                Rect parent = new Rect(0, outRect.top, 100, outRect.bottom);
                if (!parent.contains(1, (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        stickerSelection.setImageDrawable(getResources().getDrawable(R.drawable.ic_sticker));
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    void sendMessage(String message, String type) {
        if (type.equals("text") && message.equals("")) {
            type = "sticker";
            message = "like_sticker.PNG";
        }
        Map<String, Object> map = new HashMap<>();
        String temp_key = root_room_name.push().getKey();
        root_room_name.updateChildren(map);

        DatabaseReference message_root = root_room_name.child(temp_key);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", user_name);
        map2.put("msg", message);
        map2.put("state", "Đã gửi");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        map2.put("time", currentDateandTime);
        map2.put("type", type);

        message_root.updateChildren(map2);

        listMessages.add(listMessages.size() - 1, new MessagesListAdapter.Message(user_name, message, true, currentDateandTime, "Đã gửi", type));
        updateListMessages();
        listViewMessages.setSelection(listMessages.size() - 1);

        ContentValues values = new ContentValues();
        values.put("content", message);
        values.put("time", currentDateandTime);
        values.put("sender", user_name);
        values.put("state", "Đã gửi");
        values.put("type", type);
        database.insert(tableName, null, values);

        if (type.equals("text")) input_msg.setText("");
    }

    private void enablePopUpView() {
        ViewPager pager = (ViewPager) popupView.findViewById(R.id.emoticons_pager);
        StickersPagerAdapter adapter = new StickersPagerAdapter(ChatActivity.this, this);
        pager.setAdapter(adapter);
        tabSticker.setupWithViewPager(pager);
        View view1 = getLayoutInflater().inflate(R.layout.custom_tab_icon, null);
        view1.findViewById(R.id.icon_tab).setBackgroundResource(R.drawable.tab1);
        tabSticker.getTabAt(0).setCustomView(view1);
        View view2 = getLayoutInflater().inflate(R.layout.custom_tab_icon, null);
        view2.findViewById(R.id.icon_tab).setBackgroundResource(R.drawable.tab2);
        tabSticker.getTabAt(1).setCustomView(view2);
        View view3 = getLayoutInflater().inflate(R.layout.custom_tab_icon, null);
        view3.findViewById(R.id.icon_tab).setBackgroundResource(R.drawable.tab3);
        tabSticker.getTabAt(2).setCustomView(view3);
        View view4 = getLayoutInflater().inflate(R.layout.custom_tab_icon, null);
        view4.findViewById(R.id.icon_tab).setBackgroundResource(R.drawable.tab4);
        tabSticker.getTabAt(3).setCustomView(view4);
        View view5 = getLayoutInflater().inflate(R.layout.custom_tab_icon, null);
        view5.findViewById(R.id.icon_tab).setBackgroundResource(R.drawable.tab5);
        tabSticker.getTabAt(4).setCustomView(view5);
        View view6 = getLayoutInflater().inflate(R.layout.custom_tab_icon, null);
        view6.findViewById(R.id.icon_tab).setBackgroundResource(R.drawable.tab6);
        tabSticker.getTabAt(5).setCustomView(view6);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, keyboardHeight, false);
    }

    private void updateListMessages() {
        listViewMessages.setAdapter(new MessagesListAdapter(getBaseContext(), listMessages, idUserName, idRoomName));
    }

    @Override
    public void onStop() {
        root_user_name.removeEventListener(listener_user);
        root_room_name.removeEventListener(listener_room);
        super.onStop();
    }

    @Override
    public void onResume() {
        listener_user = root_user_name.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update_conservation_of_user(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_conservation_of_user(dataSnapshot);
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
        listener_room = root_room_name.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update_conservation_of_room(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_conservation_of_room(dataSnapshot);
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
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    private void update_conservation_of_room(final DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String sender = (String) ((DataSnapshot) i.next()).getValue();
            DataSnapshot state = (DataSnapshot) i.next();
            String date = (String) ((DataSnapshot) i.next()).getValue();
            String type = (String) ((DataSnapshot) i.next()).getValue();

            MessagesDatabaseHelper helper = new MessagesDatabaseHelper(getBaseContext());
            SQLiteDatabase mDatabase = helper.getWritableDatabase();
            helper.createTable(mDatabase, tableName);
            if (sender.equals(user_name) && state.getValue().equals("Đã nhận")) {
                ContentValues values = new ContentValues();
                values.put("content", chat_msg);
                values.put("time", date);
                values.put("sender", sender);
                values.put("state", "Đã nhận");
                values.put("type", type);
                mDatabase.update(tableName, values, "state" + " = ?", new String[]{"Đã nhận"});
                for (MessagesListAdapter.Message message : listMessages) {
                    if (message.date.equals(date)) message.state = "Đã nhận";
                }
                updateListMessages();
            }

            if (sender.equals(user_name) && state.getValue().equals("Đã xem")) {
                for (MessagesListAdapter.Message message : listMessages) {
                    if (message.date.equals(date)) message.state = "Đã xem";
                }
                ContentValues values = new ContentValues();
                values.put("content", chat_msg);
                values.put("time", date);
                values.put("sender", sender);
                values.put("state", "Đã xem");
                values.put("type", type);
                mDatabase.update(tableName, values, "time" + " = ?", new String[]{date});
                dataSnapshot.getRef().removeValue();
                updateListMessages();
            }
        }
    }

    private void update_conservation_of_user(final DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String sender = (String) ((DataSnapshot) i.next()).getValue();
            DataSnapshot state = (DataSnapshot) i.next();
            String date = (String) ((DataSnapshot) i.next()).getValue();
            String type = (String) ((DataSnapshot) i.next()).getValue();
//            DateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//            Date date1 = new Date();
//            try {
//                date1 = sdf.parse(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            if (sender.equals(room_name) && state.getValue().equals("Đã gửi")) {
                listMessages.add(listMessages.size() - 1, new MessagesListAdapter.Message(sender, chat_msg, sender.equals(user_name), date, "Đã xem", type));
                updateListMessages();
                ContentValues values = new ContentValues();
                values.put("content", chat_msg);
                values.put("time", date);
                values.put("sender", sender);
                values.put("state", "Đã xem");
                values.put("type", type);
                database.insert(tableName, null, values);

                state.getRef().setValue("Đã xem");
            } else if (sender.equals(room_name) && state.getValue().equals("Đã nhận")) {
                state.getRef().setValue("Đã xem");
            }
        }
    }

    @Override
    public void keyClickedIndex(final String index) {

    }

    private class MessageHandler extends Handler {
        private int size = 1;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (size == 0) return;
                    if (hasJustOpen) {
                        hasJustOpen = false;
                        return;
                    }
                    listViewMessages.addHeaderView(headerView);
                    break;
                case 1:
                    size = getMoreData();
                    int index = listViewMessages.getFirstVisiblePosition() + size;
                    View v = listViewMessages.getChildAt(listViewMessages.getHeaderViewsCount());
                    int top = (v == null) ? 0 : v.getTop();
                    updateListMessages();
                    listViewMessages.setSelectionFromTop(index, top);
                    listViewMessages.removeHeaderView(headerView);
                    isLoading = false;
                    break;
            }
        }
    }

    private int getMoreData() {
        int count = 0;
        while (count <= 15 && !cursor2.isBeforeFirst()) {
            listMessages.add(1, cursor2.getMessage(user_name));
            count++;
            cursor2.moveToPrevious();
        }
        return count;
    }

    private class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            Message msg = handler.obtainMessage(1, true);
            handler.sendMessage(msg);
        }
    }
}