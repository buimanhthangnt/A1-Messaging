package com.etspteam.a1_messaging.main.contact;

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
import com.etspteam.a1_messaging.chat_room.ChatActivity;
import com.etspteam.a1_messaging.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

class MemberAdapter extends RecyclerView.Adapter{
    private List<ListMember.Member> list;
    private Context context;

    MemberAdapter(Context c, List<ListMember.Member> l) {
        context = c;
        list = l;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_card, parent, false);
        return new MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MemberHolder memberHolder = (MemberHolder) holder;
        memberHolder.nameMember.setText(list.get(position).name);
        memberHolder.phoneMember.setText("SĐT:  " + list.get(position).phoneNuber);
        memberHolder.profile.setImageResource(list.get(position).idImage);
        memberHolder.cardMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(),ChatActivity.class);
                intent.putExtra("com.etspteam.index", list.get(position).id - 1);
                context.startActivity(intent);
            }
        });
        memberHolder.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        memberHolder.cardMember.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BottomSheetDialogContactFragment bottomSheet = new BottomSheetDialogContactFragment();
                ((Activity) context).getIntent().putExtra("com.etspteam.contactSelectedIndex", list.get(position).id - 1);
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
                return false;
            }
        });
        memberHolder.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogContactFragment bottomSheet = new BottomSheetDialogContactFragment();
                ((Activity) context).getIntent().putExtra("com.etspteam.contactSelectedIndex", list.get(position).id - 1);
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MemberHolder extends RecyclerView.ViewHolder {
        RelativeLayout cardMember;
        CircleImageView profile;
        TextView nameMember;
        TextView phoneMember;
        ImageView menuIcon;

        MemberHolder(View itemView) {
            super(itemView);
            cardMember = (RelativeLayout) itemView.findViewById(R.id.card_member);
            profile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            nameMember = (TextView) itemView.findViewById(R.id.name_member);
            phoneMember = (TextView) itemView.findViewById(R.id.phone_member);
            menuIcon = (ImageView) itemView.findViewById(R.id.icon_menu_member);
        }
    }
}
