package com.etspteam.a1_messaging.chat_room;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.contact.ListMember;

import java.io.InputStream;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

class MessagesListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messagesItems;
    private int idUser;
    private int idRoom;

    MessagesListAdapter(Context context, List<Message> navDrawerItems, int idUserName, int idRoomName) {
        this.context = context;
        this.messagesItems = navDrawerItems;
        idUser = idUserName;
        idRoom = idRoomName;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (position == messagesItems.size() - 1) {
            if (!(messagesItems.get(position - 1).isSelf) || messagesItems.get(position - 1).state.equals("")) return mInflater.inflate(R.layout.list_item_message_left, null);
            convertView = mInflater.inflate(R.layout.state_message, null);
            TextView state = (TextView) convertView.findViewById(R.id.state);
            state.setText(messagesItems.get(position - 1).state);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0,0,0,36);
            state.setLayoutParams(llp);
            return convertView;
        }

        if (messagesItems.get(position).isSelf) {
            convertView = mInflater.inflate(R.layout.list_item_message_right, null);
        } else {
            convertView = mInflater.inflate(R.layout.list_item_message_left, null);
        }

        if (position == 0) {
            return convertView;
        }

        CircleImageView imageProfile = (CircleImageView) convertView.findViewById(R.id.sender);
        ImageView sticker = (ImageView) convertView.findViewById(R.id.sticker);

        String user_name = ListMember.getList().get(idUser - 1).shortname;
        String room_name = ListMember.getList().get(idRoom - 1).shortname;

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        boolean top = false;
        boolean bottom = false;

        if (messagesItems.get(position).fromName.equals(user_name)) {
            if (position - 1 >= 0 && messagesItems.get(position - 1).fromName.equals(user_name)) {
                if (messagesItems.get(position - 1).type.equals("text")) top = true;
            }
            if (position + 1 < messagesItems.size() && messagesItems.get(position + 1).fromName.equals(user_name)) {
                if (messagesItems.get(position + 1).type.equals("text")) bottom = true;
            }
            if (!( position + 1 < messagesItems.size() && messagesItems.get(position + 1).fromName.equals(user_name) )) {
                imageProfile.setImageResource(ListMember.getList().get(idUser - 1).idImage);
            }
            if (m.type.equals("text")) {
                if (top && bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_you_middle));
                if (top && !bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_you_bottom));
                if (!top && bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_you_top));
                if (!top && !bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_you));
            } else if (m.type.equals("sticker")) {
                setImageForSticker(sticker, m.message);
            }
        } else {
            if (position - 1 >= 0 && messagesItems.get(position - 1).fromName.equals(room_name)) {
                if (messagesItems.get(position - 1).type.equals("text")) top = true;
            }
            if (position + 1 < messagesItems.size() && messagesItems.get(position + 1).fromName.equals(room_name)) {
                if (messagesItems.get(position + 1).type.equals("text")) bottom = true;
            }
            if (!(position + 1 < messagesItems.size() && messagesItems.get(position + 1).fromName.equals(room_name))) {
                imageProfile.setImageResource(ListMember.getList().get(idRoom - 1).idImage);
            }
            if (m.type.equals("text")) {
                if (top && bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_from_middle));
                if (top && !bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_from_bottom));
                if (!top && bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_from_top));
                if (!top && !bottom) txtMsg.setBackground(context.getResources().getDrawable(R.drawable.bg_msg_from));
            } else if (m.type.equals("sticker")) {
                setImageForSticker(sticker, m.message);
            }

        }
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (m.type.equals("sticker")) return convertView;
        int sideMargin = (int) DpToPixel(64);
        int sideMargin2 = (int) DpToPixel(108);
        int bottomMargin = (int) DpToPixel(10);
        if (position == messagesItems.size() - 2) bottomMargin = (int) DpToPixel(5);
        if (!bottom) {
            if (m.isSelf) llp.setMargins(sideMargin2, 0, 0, bottomMargin);
            else llp.setMargins(0, 0, sideMargin, bottomMargin);
            if (position == messagesItems.size() - 2) {
                if (m.isSelf) llp.setMargins(sideMargin2, 0, 0, bottomMargin);
                else llp.setMargins(0, 0, sideMargin, bottomMargin);
            }
            txtMsg.setLayoutParams(llp);
        } else {
            if (m.isSelf) llp.setMargins(sideMargin2, 0, 0, 0);
            else llp.setMargins(0, 0, sideMargin, 0);
            txtMsg.setLayoutParams(llp);
        }

        float leftPadding = context.getResources().getDimension(R.dimen.padding_left_message);
        float rightPadding = context.getResources().getDimension(R.dimen.padding_right_message);
        float topPadding = context.getResources().getDimension(R.dimen.padding_top_message);
        float bottomPadding = context.getResources().getDimension(R.dimen.padding_bottom_message);
        txtMsg.setPadding((int) leftPadding,(int) topPadding,(int) rightPadding,(int) bottomPadding);
        txtMsg.setText(m.message);
        return convertView;
    }

    private float DpToPixel(float dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setImageForSticker(ImageView sticker, String path) {
        AssetManager mngr = context.getAssets();
        InputStream in = null;
        try {
            in = mngr.open(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sticker.setImageBitmap(BitmapFactory.decodeStream(in, null, null));
        if (path.charAt(8) == '1') {
            sticker.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.width1);
            sticker.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.width1);
        } else if (path.charAt(8) == '3') {
            sticker.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.width2);
            sticker.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.width2);
        } else if (path.charAt(0) == 'l') {
            sticker.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.like_size);
            sticker.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.like_size);
        } else {
            sticker.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.min_width);
            sticker.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.min_width);
        }
        int paddingSticker = (int) DpToPixel(8);
        sticker.setPadding(paddingSticker, paddingSticker, paddingSticker, paddingSticker);
    }

    static class Message {
        String fromName, message;
        boolean isSelf;
        String date;
        String state;
        String type;

        Message(String fromName, String message, boolean isSelf, String d, String s, String ty) {
            this.fromName = fromName;
            this.message = message;
            this.isSelf = isSelf;
            date = d;
            state = s;
            type = ty;
        }
    }

}
