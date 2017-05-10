package com.etspteam.a1_messaging.main.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.MainActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_chat, container, false);
        ((MainActivity) getActivity()).setToolbarAndStatusBarColor(R.color.green_material, R.color.green_material_dark);
        GridView gridView = (GridView) v.findViewById(R.id.grid_group);
        List<GroupGridAdapter.Group> list = new ArrayList<>();
        List<ListMember.Member> all = ListMember.getList();
        List<ListMember.Member> list1 = new ArrayList<>();
        List<ListMember.Member> list2 = new ArrayList<>();
        List<ListMember.Member> list3 = new ArrayList<>();
        list1.add(all.get(12)); list1.add(all.get(22)); list1.add(all.get(32));
        list2.add(all.get(14)); list2.add(all.get(24)); list2.add(all.get(31)); list2.add(all.get(40));
        list3.add(all.get(11)); list3.add(all.get(22)); list3.add(all.get(31)); list3.add(all.get(41));
        list.add(new GroupGridAdapter.Group("Hội chém gió A1 THPT Bình Thanh", ListMember.getList(), ListMember.getList().get(32).idImage));
        list.add(new GroupGridAdapter.Group("Hội đập chuột", list1, all.get(12).idImage));
        list.add(new GroupGridAdapter.Group("Thanh niên nghiêm túc", list2, all.get(31).idImage));
        list.add(new GroupGridAdapter.Group("Lớp trưởng và những người bạn", list3, all.get(2).idImage));
        GroupGridAdapter adapter = new GroupGridAdapter(getActivity(), list);
        gridView.setAdapter(adapter);
        return v;
    }

}
