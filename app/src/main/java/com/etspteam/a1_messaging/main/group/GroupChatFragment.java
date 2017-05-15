package com.etspteam.a1_messaging.main.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.MainActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {
    static int[] listSelectedMember = new int[45];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_chat, container, false);
        ((MainActivity) getActivity()).setToolbarAndStatusBarColor(R.color.green_material, R.color.green_material_dark);
        final GridView gridView = (GridView) v.findViewById(R.id.grid_group);
        final List<GroupGridAdapter.Group> list = new ArrayList<>();
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

        FloatingActionButton addButton = (FloatingActionButton) v.findViewById(R.id.add_group_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 45; i++) {
                    listSelectedMember[i] = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_group, null);
                final EditText groupName = (EditText) dialogView.findViewById(R.id.group_name_edittext);
                groupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            v.clearFocus();
                            ((InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                });
                dialogView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!(v instanceof EditText)) {
                            v.clearFocus();
                            ((InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        return false;
                    }
                });
                RecyclerView listMember = (RecyclerView) dialogView.findViewById(R.id.list_member_add_group);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                listMember.setLayoutManager(layoutManager);
                listMember.setHasFixedSize(true);
                listMember.setAdapter(new MemberInGroupAdapter());
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Button cancel = (Button) dialogView.findViewById(R.id.cancel_button_in_group);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button OK = (Button) dialogView.findViewById(R.id.OK_button_in_group);
                OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (groupName.getText().toString().equals(""))
                            Toast.makeText(getContext(), "Tên nhóm không được bỏ trống", Toast.LENGTH_SHORT).show();
                        String listIndexMember = MainActivity.getuserIndex() + " ";
                        for (int i = 0; i < 45; i++)
                            if (listSelectedMember[i] == 1) listIndexMember += i + " ";
                        if (listIndexMember.length() < 6)
                            Toast.makeText(getContext(), "Nhóm phải có ít nhất 3 thành viên", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        return v;
    }
}
