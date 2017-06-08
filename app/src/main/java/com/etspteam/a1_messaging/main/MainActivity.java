package com.etspteam.a1_messaging.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.etspteam.a1_messaging.chat_room.MessagesDatabaseHelper;
import com.etspteam.a1_messaging.login_signup.LoginActivity;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.database.DataApplicationHelper;
import com.etspteam.a1_messaging.database.DataCursorWrapper;
import com.etspteam.a1_messaging.main.contact.ListMember;
import com.etspteam.a1_messaging.main.group.GroupChatFragment;
import com.etspteam.a1_messaging.main.message.MessageFragment;
import com.etspteam.a1_messaging.main.contact.ContactFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static int indexUser = 0;
    public static String userName = "";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarAndStatusBarColor(R.color.blue, R.color.bluedark);
        userName = getUserName(this);

        if (userName.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 1);
        }

        LinearLayout tab1 = (LinearLayout) findViewById(R.id.tab1);
        LinearLayout tab2 = (LinearLayout) findViewById(R.id.tab2);
        LinearLayout tab3 = (LinearLayout) findViewById(R.id.tab3);
        LinearLayout tab4 = (LinearLayout) findViewById(R.id.tab4);

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MessageFragment()).commit();
                setTitleToolbar("Tin nhắn");
                setSelectedTab1(1);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ContactFragment()).commit();
                setTitleToolbar("Bạn bè");
                setSelectedTab1(2);
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new GroupChatFragment()).commit();
                setTitleToolbar("Trò chuyện nhóm");
                setSelectedTab1(3);
            }
        });
        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new IncogtiveChatFragment()).commit();
                setTitleToolbar("Nhắn tin ẩn danh");
                setSelectedTab1(4);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MessageFragment()).commit();
        }
        setTitleToolbar("Tin nhắn");
        setSelectedTab1(1);
//        if (!userName.equals("")) {
//            Intent i = PushNotificationService.newIntent(this, userName);
//            startService(i);
//            //start service. Remember stop when activity is destroyed
//            //PushNotificationService.setServiceAlarm(this, true, userName);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            this.recreate();
        }
    }

    private void setSelectedTab1(int number) {
        switch (number) {
            case 1:
                setSelectedTab2(R.id.title_tab1, R.id.icon_tab1, R.drawable.ic_blue_message, R.color.bluedark);
                setSelectedTab2(R.id.title_tab2, R.id.icon_tab2, R.drawable.ic_contact, R.color.monsoon);
                setSelectedTab2(R.id.title_tab3, R.id.icon_tab3, R.drawable.ic_group, R.color.monsoon);
                setSelectedTab2(R.id.title_tab4, R.id.icon_tab4, R.drawable.ic_incogtive, R.color.monsoon);
                break;
            case 2:
                setSelectedTab2(R.id.title_tab1, R.id.icon_tab1, R.drawable.ic_message, R.color.monsoon);
                setSelectedTab2(R.id.title_tab2, R.id.icon_tab2, R.drawable.ic_blue_contact, R.color.gmail_dark);
                setSelectedTab2(R.id.title_tab3, R.id.icon_tab3, R.drawable.ic_group, R.color.monsoon);
                setSelectedTab2(R.id.title_tab4, R.id.icon_tab4, R.drawable.ic_incogtive, R.color.monsoon);
                break;
            case 3:
                setSelectedTab2(R.id.title_tab1, R.id.icon_tab1, R.drawable.ic_message, R.color.monsoon);
                setSelectedTab2(R.id.title_tab2, R.id.icon_tab2, R.drawable.ic_contact, R.color.monsoon);
                setSelectedTab2(R.id.title_tab3, R.id.icon_tab3, R.drawable.ic_blue_group, R.color.green_material_dark);
                setSelectedTab2(R.id.title_tab4, R.id.icon_tab4, R.drawable.ic_incogtive, R.color.monsoon);
                break;
            case 4:
                setSelectedTab2(R.id.title_tab1, R.id.icon_tab1, R.drawable.ic_message, R.color.monsoon);
                setSelectedTab2(R.id.title_tab2, R.id.icon_tab2, R.drawable.ic_contact, R.color.monsoon);
                setSelectedTab2(R.id.title_tab3, R.id.icon_tab3, R.drawable.ic_group, R.color.monsoon);
                setSelectedTab2(R.id.title_tab4, R.id.icon_tab4, R.drawable.ic_blue_incogtive, R.color.incogtive_dark);
                break;
        }
    }

    private void setSelectedTab2(int idTitle, int idIcon, int idImage, int idColorText) {
        TextView title = (TextView) findViewById(idTitle);
        title.setTextColor(getResources().getColor(idColorText));
        ImageView iconTab = (ImageView) findViewById(idIcon);
        iconTab.setImageDrawable(getResources().getDrawable(idImage));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_out) {
            DataApplicationHelper dataHelper = new DataApplicationHelper(this);
            SQLiteDatabase mDatabase = dataHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("key", "user_name");
            values.put("value", "");
            mDatabase.update("settings", values, "key" + " = ?", new String[]{"user_name"});

            MessagesDatabaseHelper messageDbHelper = new MessagesDatabaseHelper(this);
            messageDbHelper.deleteallDatabase();
            this.recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    void setTitleToolbar(String titleToolbar) {
        toolbar.setTitle("");
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        Typeface roboto = Typeface.createFromAsset(getAssets(), "font/Roboto-Medium.ttf");
        title.setTypeface(roboto);
        title.setText(titleToolbar);
    }

    public static String getUserName(Context context) {
        SQLiteDatabase mDatabase = new DataApplicationHelper(context).getWritableDatabase();
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
        return pair.second;
    }

    public static int getuserIndex() {
        for (ListMember.Member member: ListMember.getList())
            if (userName.equals(member.shortname)) indexUser = member.id - 1;
        return indexUser;
    }

    public static List<ListMember.Member> getListWithoutUser() {
        List<ListMember.Member> list = new ArrayList<>();
        for (ListMember.Member member: ListMember.getList()) {
            if (!member.shortname.equals(userName)) list.add(member);
        }
        return list;
    }

    public void setToolbarAndStatusBarColor(int resTool, int resStatus) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(resTool)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, resStatus));
        }
    }
}
