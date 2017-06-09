package com.etspteam.a1_messaging.main.group;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.chat_room.KeyboardHeightObserver;
import com.etspteam.a1_messaging.chat_room.KeyboardHeightProvider;
import com.etspteam.a1_messaging.chat_room.StickersGridAdapter;
import com.etspteam.a1_messaging.chat_room.StickersPagerAdapter;
import com.etspteam.a1_messaging.database.DataApplicationHelper;
import com.etspteam.a1_messaging.database.DataCursorWrapper;
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

public class GroupChatActivity extends AppCompatActivity implements StickersGridAdapter.KeyClickListener, KeyboardHeightObserver {

    private EditText input_msg;
    private List<GroupMessagesListAdapter.Message> listMessages;
    private String user_name;
    private DatabaseReference root_group;
    private String group_name;
    private ChildEventListener listener_group;
    private ListView listViewMessages;
    private View popupView;
    private PopupWindow popupWindow;
    private int keyboardHeight;
    private LinearLayout rootLayout;
    private InputMethodManager imm;
    private ImageView stickerSelection;
    private TabLayout tabSticker;
    private KeyboardHeightProvider keyboardHeightProvider;
    private String listMembers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        rootLayout = (LinearLayout) findViewById(R.id.group_chat_activity);
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

        final ImageView btn_send_msg = (ImageView) findViewById(R.id.group_btn_send);
        input_msg = (EditText) findViewById(R.id.group_msg_input);
        listViewMessages = (ListView) findViewById(R.id.group_list_view_messages);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = getLayoutInflater().inflate(R.layout.sticker_popup, null);
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
        group_name = getIntent().getStringExtra("com.etspteam.groupName");
        listMembers = getIntent().getStringExtra("com.etspteam.members");
        setTitle(group_name);
        root_group = FirebaseDatabase.getInstance().getReference().child("group").child("group_info").child(group_name);

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
        listMessages.add(new GroupMessagesListAdapter.Message("", "", true, "", "", "text"));
        listMessages.add(new GroupMessagesListAdapter.Message("", "", true, "", "", "text"));

        GroupMessagesListAdapter adapter = new GroupMessagesListAdapter(this, listMessages, listMembers);
        listViewMessages.setAdapter(adapter);

        root_group.addChildEventListener(new ChildEventListener() {
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
        String temp_key = root_group.push().getKey();
        root_group.updateChildren(map);

        DatabaseReference message_root = root_group.child(temp_key);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", user_name);
        map2.put("msg", message);
        map2.put("state", "Đã gửi");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        map2.put("time", currentDateandTime);
        map2.put("type", type);

        message_root.updateChildren(map2);

        listMessages.add(listMessages.size() - 1, new GroupMessagesListAdapter.Message(user_name, message, true, currentDateandTime, "Đã gửi", type));
        updateListMessages();
        listViewMessages.setSelection(listMessages.size() - 1);
        if (type.equals("text")) input_msg.setText("");
    }

    private void enablePopUpView() {
        ViewPager pager = (ViewPager) popupView.findViewById(R.id.emoticons_pager);
        pager.setAdapter(new StickersPagerAdapter(GroupChatActivity.this, this));
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
        listViewMessages.setAdapter(new GroupMessagesListAdapter(getBaseContext(), listMessages, listMembers));
    }

    @Override
    public void onStop() {
        root_group.removeEventListener(listener_group);
        super.onStop();
    }

    @Override
    public void onResume() {
        listener_group = root_group.addChildEventListener(new ChildEventListener() {
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
        String chat_msg, sender, date, type;
        DataSnapshot state;

        while (i.hasNext()) {
            chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            sender = (String) ((DataSnapshot) i.next()).getValue();
            state = (DataSnapshot) i.next();
            date = (String) ((DataSnapshot) i.next()).getValue();
            type = (String) ((DataSnapshot) i.next()).getValue();

            if (state.getValue().equals("Đã gửi")) {
                listMessages.add(listMessages.size() - 1, new GroupMessagesListAdapter.Message(sender, chat_msg, sender.equals(user_name), date, "Đã xem", type));
                updateListMessages();
                state.getRef().setValue("Đã xem");
            } else if (state.getValue().equals("Đã nhận")) {
                for (GroupMessagesListAdapter.Message message : listMessages) {
                    if (message.date.equals(date)) message.state = "Đã nhận";
                }
                updateListMessages();
            }
            if (state.getValue().equals("Đã xem")) {
                for (GroupMessagesListAdapter.Message message : listMessages) {
                    if (message.date.equals(date)) message.state = "Đã xem";
                }
                updateListMessages();
            }
        }
    }

    @Override
    public void keyClickedIndex(final String index) {

    }
}