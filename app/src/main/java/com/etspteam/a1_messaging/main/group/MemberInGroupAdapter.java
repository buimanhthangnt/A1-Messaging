package com.etspteam.a1_messaging.main.group;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.MainActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;

import java.util.List;

class MemberInGroupAdapter extends RecyclerView.Adapter {
    private List<ListMember.Member> list = MainActivity.getListWithoutUser();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberInGroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.member_in_group_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MemberInGroupViewHolder member = (MemberInGroupViewHolder) holder;
        final int[] listSM = GroupChatFragment.listSelectedMember;
        final int indexInList = list.get(position).id - 1;
        member.nameMember.setText(list.get(position).name);
        member.isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) listSM[indexInList] = 1;
                else listSM[indexInList] = 0;
            }
        });
        if (listSM[indexInList] == 1) member.isSelected.setChecked(true);
        else member.isSelected.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MemberInGroupViewHolder extends RecyclerView.ViewHolder {
        TextView nameMember;
        CheckBox isSelected;
        RelativeLayout member;

        MemberInGroupViewHolder(View itemView) {
            super(itemView);
            member = (RelativeLayout) itemView.findViewById(R.id.member_in_group);
            nameMember = (TextView) itemView.findViewById(R.id.member_name_in_group);
            isSelected = (CheckBox) itemView.findViewById(R.id.member_is_selected_in_group);
        }
    }
}
