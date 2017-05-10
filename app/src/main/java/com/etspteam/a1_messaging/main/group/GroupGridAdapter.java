package com.etspteam.a1_messaging.main.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.contact.ListMember;

import java.util.List;

class GroupGridAdapter extends BaseAdapter {
    private List<Group> listGroups;
    private Context context;

    GroupGridAdapter(Context c, List<Group> l) {
        context = c;
        listGroups = l;
    }

    @Override
    public int getCount() {
        return listGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return listGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_item, null);
        }
        Group group = listGroups.get(position);
        ImageView profile = (ImageView) view.findViewById(R.id.group_profile);
        profile.setImageDrawable(context.getResources().getDrawable(group.idProfile));
        TextView name = (TextView) view.findViewById(R.id.group_name);
        name.setText(group.name);
        TextView members = (TextView) view.findViewById(R.id.group_members);
        List<ListMember.Member> listMember = group.listMembers;
        String temp = "";
        for (int i = 0; i < listMember.size(); i++) {
            temp += listMember.get(i).beautifulName;
            if (i != listMember.size() - 1) temp += ", ";
        }
        members.setText(temp);
        return view;
    }

    static class Group {
        String name;
        List<ListMember.Member> listMembers;
        int idProfile;

        Group(String n, List<ListMember.Member> l, int i) {
            name = n;
            listMembers = l;
            idProfile = i;
        }
    }
}
