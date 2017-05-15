package com.etspteam.a1_messaging.main.contact;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.etspteam.a1_messaging.chat_room.ChatActivity;
import com.etspteam.a1_messaging.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class FavoriteFriendAdapter extends RecyclerView.Adapter{
    private List<ListMember.Member> list;
    private Context context;

    FavoriteFriendAdapter(Context c, List<ListMember.Member> l) {
        context = c;
        list = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_friend_item, parent, false);
        return new FavoriteFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FavoriteFriendHolder memberHolder = (FavoriteFriendHolder) holder;
        int indexMember = 0;
        for (int i = 0; i < ListMember.getList().size(); i++) {
            if (ListMember.getList().get(i).idImage == list.get(position).idImage) {
                indexMember = i;
                break;
            }
        }
        final int finalIndex = indexMember;
        memberHolder.nameMember.setText(list.get(position).beautifulName);
        memberHolder.profile.setImageResource(list.get(position).idImage);
        memberHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(),ChatActivity.class);
                intent.putExtra("com.etspteam.index", finalIndex);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class FavoriteFriendHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView nameMember;

        FavoriteFriendHolder(View itemView) {
            super(itemView);
            profile = (CircleImageView) itemView.findViewById(R.id.favorite_image_frofile);
            nameMember = (TextView) itemView.findViewById(R.id.favorite_name);
        }
    }
}
