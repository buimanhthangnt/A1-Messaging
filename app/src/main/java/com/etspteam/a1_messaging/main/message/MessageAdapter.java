package com.etspteam.a1_messaging.main.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.chat_room.ChatActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

class MessageAdapter extends RecyclerView.Adapter {
    private List<List<String>> listInfo;
    private Context context;

    MessageAdapter(Context c, List<List<String>> l) {
        context = c;
        listInfo = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageAdapter.MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MessageAdapter.MessageHolder memberHolder = (MessageAdapter.MessageHolder) holder;
        int stt = Integer.parseInt(listInfo.get(position).get(0));
        memberHolder.nameMember.setText(ListMember.getList().get(stt).name);
        String content = "";
        if (listInfo.get(position).get(1).equals("true")) {
            content += "Bạn: ";
        }
        if (listInfo.get(position).get(5).equals("sticker")) {
            memberHolder.content.setText(content + "[ Sticker ]");
        }
        else memberHolder.content.setText(content + listInfo.get(position).get(2));
        int resId = ListMember.getList().get(stt).idImage;
        memberHolder.profile.setImageDrawable(context.getResources().getDrawable(resId));
        if (position == listInfo.size() - 1) memberHolder.divider.setVisibility(View.INVISIBLE);
        final int index = stt;
        memberHolder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("com.etspteam.index", index);
                context.startActivity(i);
            }
        });
        memberHolder.message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BottomSheetDialogMessageFragment bottomSheet = new BottomSheetDialogMessageFragment();
                ((Activity) context).getIntent().putExtra("com.etspteam.selectedIndex", index);
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listInfo.size();
    }

    private class MessageHolder extends RecyclerView.ViewHolder {
        RelativeLayout message;
        CircleImageView profile;
        TextView nameMember;
        TextView content;
        ImageView newMessage;
        View divider;

        MessageHolder(View itemView) {
            super(itemView);
            message = (RelativeLayout) itemView.findViewById(R.id.card_message);
            profile = (CircleImageView) itemView.findViewById(R.id.profile_image_message);
            nameMember = (TextView) itemView.findViewById(R.id.name_sender);
            content = (TextView) itemView.findViewById(R.id.message_content);
            newMessage = (ImageView) itemView.findViewById(R.id.new_message);
            divider = itemView.findViewById(R.id.messages_divider);
        }
    }
}
