package com.etspteam.a1_messaging;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.etspteam.a1_messaging.chat_room.MessagesDatabaseHelper;
import com.etspteam.a1_messaging.main.MainActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;
import com.etspteam.a1_messaging.main.message.MessageFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Iterator;

public class PushNotificationService extends IntentService {
    private static String userName;

    public static Intent newIntent(Context context, String un) {
        userName = un;
        return new Intent(context, PushNotificationService.class);
    }

    public PushNotificationService() {
        super("PushNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Service start", "fnewiofuwofjweofowfuowfiuowu");
        if (!isNetworkConnected()) return;
        DatabaseReference rootUserName = FirebaseDatabase.getInstance().getReference().child("messages").child(userName);
        rootUserName.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                update_and_push_new_message(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                update_and_push_new_message(dataSnapshot);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }

    private void update_and_push_new_message(final DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            String chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            String sender = (String) ((DataSnapshot) i.next()).getValue();
            DataSnapshot state = (DataSnapshot) i.next();
            String date = (String) ((DataSnapshot) i.next()).getValue();
            String type = (String) ((DataSnapshot) i.next()).getValue();
            if (state.getValue().equals("Đã gửi")) {
                ContentValues values = new ContentValues();
                values.put("content", chat_msg);
                values.put("time", date);
                values.put("sender", sender);
                values.put("state", "Đã nhận");
                values.put("type", type);
                SQLiteDatabase database = new MessagesDatabaseHelper(this).getWritableDatabase();
                database.insert(MessageFragment.getTableNameFromSenderName(sender,userName), null, values);
                state.getRef().setValue("Đã nhận");
                Intent intent = PushNotificationService.newIntent(this, MainActivity.userName);
                PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
                String content_message = chat_msg;
                if (type.equals("sticker")) content_message = "[ sticker ]";
                String noti_title = "A1 Messaging";
                for (ListMember.Member member: ListMember.getList()) {
                    if (member.shortname.equals(sender)) {
                        noti_title = member.name;
                        break;
                    }
                }
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                        .setTicker("A1 Messaging")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(noti_title)
                        .setContentText(content_message)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH);
                if (Build.VERSION.SDK_INT >= 21) notification.setVibrate(new long[0]);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(0, notification.build());
                Log.d("Push Notification", "fnewiofuwofjweofowfuowffeerieoiteoptiepoigpoegiegiuowu");
            }
        }
    }

    public static void setServiceAlarm(Context context, boolean isOn, String un) {
        Intent i = PushNotificationService.newIntent(context,un);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            int UPDATE_INTERVAL = 60000;
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), UPDATE_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
