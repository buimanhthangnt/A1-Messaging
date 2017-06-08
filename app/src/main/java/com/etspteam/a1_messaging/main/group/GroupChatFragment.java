package com.etspteam.a1_messaging.main.group;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.etspteam.a1_messaging.R;
import com.etspteam.a1_messaging.main.MainActivity;
import com.etspteam.a1_messaging.main.contact.ListMember;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatFragment extends Fragment {
    static int[] listSelectedMember = new int[45];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_chat, container, false);
        ((MainActivity) getActivity()).setToolbarAndStatusBarColor(R.color.green_material, R.color.green_material_dark);
        final DatabaseReference branch = FirebaseDatabase.getInstance().getReference().child("group").child("group_info");
        final GridView gridView = (GridView) v.findViewById(R.id.grid_group);
        final List<GroupGridAdapter.Group> list = new ArrayList<>();
        branch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupName;
                String members;
                int idImage;
                for (DataSnapshot o : dataSnapshot.getChildren()) {
                    groupName = (String) ((HashMap)(o).getValue()).get("user_name");
                    members = (String) ((HashMap)(o).getValue()).get("members");
                    idImage = ListMember.getList()
                            .get(Integer.parseInt(members.charAt(0) + "" + members.charAt(1))).idImage;
                    if (getListMembers(members) == null) continue;
                    list.add(new GroupGridAdapter.Group(groupName, getListMembers(members), idImage, members));
                }
                gridView.setAdapter(new GroupGridAdapter(getActivity(), list));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        if (groupName.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "Tên nhóm không được bỏ trống", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int userIndex = MainActivity.getuserIndex();
                        String listIndexMember = ((userIndex < 10) ? "0" : "") + userIndex + " ";
                        for (int i = 0; i < 45; i++)
                            if (listSelectedMember[i] == 1) {
                                if (i < 10) listIndexMember += "0" + i + " ";
                                else listIndexMember += i + " ";
                            }
                        if (listIndexMember.length() < 6) {
                            Toast.makeText(getContext(), "Nhóm phải có ít nhất 3 thành viên", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!isOnline()) {
                            Toast.makeText(getContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final MaterialDialog progressDialog = new MaterialDialog.Builder(getContext())
                                .backgroundColor(getResources().getColor(R.color.white))
                                .content("   Đang tạo nhóm")
                                .canceledOnTouchOutside(false)
                                .progress(true, 0).show();
                        progressDialog.show();
                        final String gname = groupName.getText().toString();
                        final String finalListIndexMember = listIndexMember;
                        branch.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(gname)) {
                                    Toast.makeText(getContext(), "Tên nhóm đã được sử dụng", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                } else {
                                    DatabaseReference users_root = branch.child(gname);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("user_name", gname);
                                    map.put("members", finalListIndexMember);
                                    users_root.updateChildren(map);
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                });
            }
        });
        return v;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private List<ListMember.Member> getListMembers(String s) {
        List<ListMember.Member> list = new ArrayList<>();
        boolean isValid = false;
        for (int i = 0; i < s.length(); i = i + 3) {
            int index = Integer.parseInt(s.charAt(i) + "" + s.charAt(i+1));
            if (ListMember.getList().get(index).shortname.equals(MainActivity.userName)) isValid = true;
            list.add(ListMember.getList().get(index));
        }
        if (!isValid) return null;
        return list;
    }
}
